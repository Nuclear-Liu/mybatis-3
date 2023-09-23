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
package org.apache.ibatis.plugin;

import java.util.Properties;

/**
 * 拦截器接口.
 *
 * @author Clinton Begin
 */
public interface Interceptor {

  /**
   * 执行拦截逻辑.
   *
   * @param invocation
   *
   * @return
   *
   * @throws Throwable
   */
  Object intercept(Invocation invocation) throws Throwable;

  /**
   * 决定是否触发 {@link Interceptor#intercept(Invocation)} 方法.
   *
   * @param target
   *
   * @return
   */
  default Object plugin(Object target) {
    /**
     * 拦截器装饰方法，对目标对象做增强.
     */
    return Plugin.wrap(target, this);
  }

  /**
   * 根据 {@link Properties} 对象中的属性初始化 {@link Interceptor} 对象.
   *
   * @param properties
   */
  default void setProperties(Properties properties) {
    // NOP
  }

}
