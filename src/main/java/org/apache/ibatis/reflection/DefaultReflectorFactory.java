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
package org.apache.ibatis.reflection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.ibatis.util.MapUtil;

/**
 * 默认 {@link Reflector} 工厂实现类.
 */
public class DefaultReflectorFactory implements ReflectorFactory {
  /**
   * 标识是否允许缓存创建的 {@link Reflector} 对象.
   */
  private boolean classCacheEnabled = true;
  /**
   * 缓存 {@link Reflector} 对象容器. key: {@link Class<?>} value: {@link Class<?>}'s {@link Reflector}
   */
  private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

  public DefaultReflectorFactory() {
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public boolean isClassCacheEnabled() {
    return classCacheEnabled;
  }

  /**
   * {@inheritDoc}
   *
   * @param classCacheEnabled
   */
  @Override
  public void setClassCacheEnabled(boolean classCacheEnabled) {
    this.classCacheEnabled = classCacheEnabled;
  }

  /**
   * {@inheritDoc}
   *
   * @param type
   *
   * @return
   */
  @Override
  public Reflector findForClass(Class<?> type) {
    if (classCacheEnabled) {
      // synchronized (type) removed see issue #461
      /**
       * 缓存中存在，则返回缓存的 {@link Reflector} 对象，如果没有则创建并放入缓存并返回.
       */
      return MapUtil.computeIfAbsent(reflectorMap, type, Reflector::new);
    }
    return new Reflector(type);
  }

}
