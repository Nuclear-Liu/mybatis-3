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
package org.apache.ibatis.reflection.property;

import java.lang.reflect.Field;

import org.apache.ibatis.reflection.Reflector;

/**
 * 属性拷贝工具类.
 *
 * @author Clinton Begin
 */
public final class PropertyCopier {

  private PropertyCopier() {
    // Prevent Instantiation of Static Class
  }

  /**
   * 将<code>sourceBean</code>对象属性拷贝到<code>destinationBean</code>对象.
   *
   * @param type
   *          被拷贝属性所属类
   * @param sourceBean
   *          源对象
   * @param destinationBean
   *          目标对象
   */
  public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean) {
    Class<?> parent = type;
    /**
     * 递归拷贝指定 <code>type</code> 类属性.
     */
    while (parent != null) {
      /**
       * 获取需要拷贝类中的所有字段
       */
      final Field[] fields = parent.getDeclaredFields();
      for (Field field : fields) {
        try {
          try {
            /**
             * 属性拷贝
             */
            field.set(destinationBean, field.get(sourceBean));
          } catch (IllegalAccessException e) {
            /**
             * 检查权限
             */
            if (!Reflector.canControlMemberAccessible()) {
              throw e;
            }
            /**
             * 更新权限
             */
            field.setAccessible(true);
            /**
             * 属性拷贝
             */
            field.set(destinationBean, field.get(sourceBean));
          }
        } catch (Exception e) {
          // Nothing useful to do, will only fail on final fields, which will be ignored.
        }
      }
      /**
       * 获取当前类的父类，继续拷贝
       */
      parent = parent.getSuperclass();
    }
  }

}
