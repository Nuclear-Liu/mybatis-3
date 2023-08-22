/*
 *    Copyright 2009-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.cache.decorators;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;

/**
 * <p>
 * Simple blocking decorator
 * <p>
 * 简单阻塞装饰器
 * <p>
 * Simple and inefficient version of EhCache's BlockingCache decorator. It sets a lock over a cache key when the element
 * is not found in cache. This way, other threads will wait until this element is filled instead of hitting the
 * database.
 * <p>
 * EhCache <code>BlockingCache</code> 装饰器的简单低效版本。 当缓存中找不到元素时，它会对缓存 key 设置锁定。 这样，其他线程就会等待该元素被阻塞，而不是访问数据库。
 * <p>
 * By its nature, this implementation can cause deadlock when used incorrectly.
 * <p>
 * 如果使用不当，这种实现方式可能会导致死锁.
 *
 * @author Eduardo Macarron
 */
public class BlockingCache implements Cache {

  /**
   * 阻塞超时时间.
   */
  private long timeout;
  /**
   * 被装饰对象.
   */
  private final Cache delegate;
  /**
   * 线程安全锁容器：其中的 key 与实际存储 {@link java.util.HashMap} 中的每一个 key 一一对应.
   */
  private final ConcurrentHashMap<Object, CountDownLatch> locks;

  public BlockingCache(Cache delegate) {
    this.delegate = delegate;
    this.locks = new ConcurrentHashMap<>();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  /**
   * {@inheritDoc}
   * <p>
   * 添加新的缓存对象，不加锁，及即使已经存在响应 key 的缓存对象，也直接方法新值对象，写锁
   * </p>
   */
  @Override
  public void putObject(Object key, Object value) {
    try {
      delegate.putObject(key, value);
    } finally {
      releaseLock(key);
    }
  }

  @Override
  public Object getObject(Object key) {
    /* 获取锁 */
    acquireLock(key);
    /* 实际缓存对象 */
    Object value = delegate.getObject(key);
    if (value != null) {
      releaseLock(key);
    }
    return value;
  }

  @Override
  public Object removeObject(Object key) {
    // despite its name, this method is called only to release locks
    releaseLock(key);
    return null;
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  /**
   * 获取锁.
   *
   * @param key
   */
  private void acquireLock(Object key) {
    /**
     * 生成一把新门闩锁.
     */
    CountDownLatch newLatch = new CountDownLatch(1);
    while (true) {
      /**
       * putIfAbsent 线程安全，方法，只会有一个线程可以争取设置门闩锁. 1. 如果返回对象为 null ，当前线程在锁争夺中获胜，并设置了门闩锁对象，将获得数据库查询权力，其他线程将等待门闩锁释放； 2.
       * 如果返回门闩锁对象存在，当前线程在锁竞争中失败，持有该门闩锁等待门闩的释放，获取其他线程查询的结果.
       */
      CountDownLatch latch = locks.putIfAbsent(key, newLatch);
      if (latch == null) { /* 首次添加 */
        break;
      }
      /* 门闩锁对象已经存在缓存中：表示已经有一个线程设置了门闩，及有主动查询数据库权力，其他线程等待门闩释放，获取结果 */
      try {
        if (timeout > 0) { /* 如果设置了等待超时时间，调用等待锁超时 */
          boolean acquired = latch.await(timeout, TimeUnit.MILLISECONDS);
          if (!acquired) {
            throw new CacheException(
                "Couldn't get a lock in " + timeout + " for the key " + key + " at the cache " + delegate.getId());
          }
        } else { /* 没有设置超时时间，调用锁等待 */
          latch.await();
        }
      } catch (InterruptedException e) {
        throw new CacheException("Got interrupted while trying to acquire lock for key " + key, e);
      }
    }
  }

  /**
   * 释放锁. 1. 当前 key 在缓存中不存在 2. 当前 key 在缓存中已经存在
   *
   * @param key
   *          cache's id
   */
  private void releaseLock(Object key) {
    /**
     * 从锁容器中删除将要存储的 key. 1. 假设 <code>key</code> 不存在缓存中: 删除锁返回 <code>null</code> . 2. 假设 <code>key</code> 存在缓存中:
     * 删除返回实际的锁对象
     */
    CountDownLatch latch = locks.remove(key);
    if (latch == null) { /* 释放的锁不存在，说明当前对象没有被缓存 */
      throw new IllegalStateException("Detected an attempt at releasing unacquired lock. This should never happen.");
    }
    latch.countDown(); /* 释放锁 */
  }

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }
}
