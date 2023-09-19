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

import java.util.Locale;

import org.apache.ibatis.reflection.ReflectionException;

/**
 * 属性名称工具类.
 * <p/>
 * 方法以<code>is</code> <code>get</code> <code>set</code> 开头：截取获得属性名，并将首字母小写.
 *
 * @author Clinton Begin
 */
public final class PropertyNamer {

  private PropertyNamer() {
    // Prevent Instantiation of Static Class
  }

  /**
   * 方法以<code>is</code> <code>get</code> <code>set</code> 开头：截取获得属性名，并将首字母小写.
   *
   * @param name
   *          方法名
   *
   * @return
   */
  public static String methodToProperty(String name) {
    /**
     * 判断是否为<code>is</code>开头.
     */
    if (name.startsWith("is")) {
      /**
       * 截取属性名
       */
      name = name.substring(2);
    } else if (name.startsWith("get") || name.startsWith("set")) {
      /**
       * 截取属性名
       */
      name = name.substring(3);
    } else {
      throw new ReflectionException(
          "Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
    }

    if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
      /**
       * 属性首字母变为小写
       */
      name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
    }

    return name;
  }

  /**
   * 判断方法名称是否为属性.
   *
   * @param name
   *          属性方法名
   *
   * @return
   */
  public static boolean isProperty(String name) {
    return isGetter(name) || isSetter(name);
  }

  /**
   * 判断方法名称为获取器.
   *
   * @param name
   *
   * @return
   */
  public static boolean isGetter(String name) {
    return name.startsWith("get") && name.length() > 3 || name.startsWith("is") && name.length() > 2;
  }

  /**
   * 判断方法名是否为设置器.
   *
   * @param name
   *
   * @return
   */
  public static boolean isSetter(String name) {
    return name.startsWith("set") && name.length() > 3;
  }

}
