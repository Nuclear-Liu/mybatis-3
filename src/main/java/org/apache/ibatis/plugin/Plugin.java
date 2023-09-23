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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.util.MapUtil;

/**
 * 插件.
 * <p/>
 * 实现 {@link InvocationHandler} 接口，说明是通过代理对象的方式.
 *
 * @author Clinton Begin
 */
public class Plugin implements InvocationHandler {

  /**
   * 目标对象.
   */
  private final Object target;
  /**
   * 拦截器.
   */
  private final Interceptor interceptor;
  /**
   * 记录 {@link Signature} 标记的信息.
   */
  private final Map<Class<?>, Set<Method>> signatureMap;

  private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
    this.target = target;
    this.interceptor = interceptor;
    this.signatureMap = signatureMap;
  }

  /**
   * 创建目标对象的代理对象.
   *
   * @param target
   *          目标对象(被拦截对象)
   * @param interceptor
   *          拦截器
   *
   * @return
   */
  public static Object wrap(Object target, Interceptor interceptor) {
    /**
     * 获取用户自定义 {@link Interceptor} 中 {@link Signature} 注解信息.
     * <p/>
     * {@link Plugin#getSignatureMap(Interceptor)} 负责处理 {@link Signature} 注解 {@link Plugin#interceptor} 自定义的拦截器. key:
     * 拦截对象 value: 被拦截方法
     */
    Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
    /**
     * 获取目标类型
     */
    Class<?> type = target.getClass();
    /**
     * 获取目标类型实现的所有接口
     */
    Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
    if (interfaces.length > 0) {
      /**
       * 目标类型有实现接口，创建代理对象并返回.
       */
      return Proxy.newProxyInstance(type.getClassLoader(), interfaces, new Plugin(target, interceptor, signatureMap));
    }
    /**
     * 目标类型没有实现接口，返回目标对象.
     */
    return target;
  }

  /**
   * {@inheritDoc}.
   *
   * @param proxy
   *          the proxy instance that the method was invoked on
   * @param method
   *          the {@code Method} instance corresponding to the interface method invoked on the proxy instance. The
   *          declaring class of the {@code Method} object will be the interface that the method was declared in, which
   *          may be a superinterface of the proxy interface that the proxy class inherits the method through.
   * @param args
   *          an array of objects containing the values of the arguments passed in the method invocation on the proxy
   *          instance, or {@code null} if interface method takes no arguments. Arguments of primitive types are wrapped
   *          in instances of the appropriate primitive wrapper class, such as {@code java.lang.Integer} or
   *          {@code java.lang.Boolean}.
   *
   * @return
   *
   * @throws Throwable
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      Set<Method> methods = signatureMap.get(method.getDeclaringClass());
      if (methods != null && methods.contains(method)) {
        return interceptor.intercept(new Invocation(target, method, args));
      }
      return method.invoke(target, args);
    } catch (Exception e) {
      throw ExceptionUtil.unwrapThrowable(e);
    }
  }

  /**
   * 获取自定义拦截器信息： key: 拦截对象 value: 被拦截方法
   *
   * @param interceptor
   *          拦截器对象
   *
   * @return
   */
  private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
    /**
     * 获取 interceptor 对象类上的 {@link Intercepts} 注解
     */
    Intercepts interceptsAnnotation = interceptor.getClass().getAnnotation(Intercepts.class);
    // issue #251
    if (interceptsAnnotation == null) {
      throw new PluginException(
          "No @Intercepts annotation was found in interceptor " + interceptor.getClass().getName());
    }
    /**
     * 获取 {@link Intercepts} 注解的值，即要拦截方法的签名，可能包含多个 {@link Signature} 注解内容.
     */
    Signature[] sigs = interceptsAnnotation.value();
    Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();
    /**
     * 遍历所有拦截方法的签名.
     */
    for (Signature sig : sigs) {
      Set<Method> methods = MapUtil.computeIfAbsent(signatureMap, sig.type(), k -> new HashSet<>());
      try {
        Method method = sig.type().getMethod(sig.method(), sig.args());
        methods.add(method);
      } catch (NoSuchMethodException e) {
        throw new PluginException("Could not find method on " + sig.type() + " named " + sig.method() + ". Cause: " + e,
            e);
      }
    }
    return signatureMap;
  }

  private static Class<?>[] getAllInterfaces(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) {
    Set<Class<?>> interfaces = new HashSet<>();
    while (type != null) {
      for (Class<?> c : type.getInterfaces()) {
        if (signatureMap.containsKey(c)) {
          interfaces.add(c);
        }
      }
      type = type.getSuperclass();
    }
    return interfaces.toArray(new Class<?>[0]);
  }

}
