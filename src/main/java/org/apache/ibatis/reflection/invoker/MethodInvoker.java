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
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.ibatis.reflection.Reflector;

/**
 * @author Clinton Begin
 */
public class MethodInvoker implements Invoker {

  private final Class<?> type;
  private final Method method;

  /**
   * 初始化 type 与 method 属性变量.
   * @param method
   */
  public MethodInvoker(Method method) {
    this.method = method;

    /**
     * 判断方法参数是否为 1 个.
     */
    if (method.getParameterTypes().length == 1) {
      /**
       * 方法参数为 1 个，表示方法为 setter 方法， type 为形参类型
       */
      type = method.getParameterTypes()[0];
    } else {
      /**
       * 方法形参不为 1 即 0，表示方法为 getter 方法， type 为返回类型
       */
      type = method.getReturnType();
    }
  }

  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    try {
      /**
       * 执行方法
       */
      return method.invoke(target, args);
    } catch (IllegalAccessException e) {
      /**
       * 权限检查
       */
      if (Reflector.canControlMemberAccessible()) {
        method.setAccessible(true);
        return method.invoke(target, args);
      }
      throw e;
    }
  }

  @Override
  public Class<?> getType() {
    return type;
  }
}
