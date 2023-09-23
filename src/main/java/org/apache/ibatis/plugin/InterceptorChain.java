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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 拦截器链.
 *
 * @author Clinton Begin
 */
public class InterceptorChain {

  /**
   * 保存拦截器链中的所有 {@link Interceptor} 拦截器对象.
   */
  private final List<Interceptor> interceptors = new ArrayList<>();

  /**
   * 创建拦截器代理对象.
   *
   * @param target
   *
   * @return
   */
  public Object pluginAll(Object target) {
    /**
     * 遍历拦截器链中所有相关的拦截器.
     */
    for (Interceptor interceptor : interceptors) {
      /**
       * 创建拦截器的代理对象.
       */
      target = interceptor.plugin(target);
    }
    return target;
  }

  public void addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
  }

  public List<Interceptor> getInterceptors() {
    return Collections.unmodifiableList(interceptors);
  }

}
