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

import java.util.Iterator;

/**
 * 属性分词器.
 *
 * @author Clinton Begin
 */
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {
  /**
   * 表达式名称.
   */
  private String name;
  /**
   * 表达式索引名称
   */
  private final String indexedName;
  /**
   * 索引下标：例如 arr[0]=>0 map[key]=>key
   */
  private String index;
  /**
   * 子表达式
   */
  private final String children;

  /**
   * 分词实现.
   *
   * @param fullname
   */
  public PropertyTokenizer(String fullname) {
    /**
     * 获取 <code>.</code> 的下标.
     */
    int delim = fullname.indexOf('.');
    /**
     * 判断是否存在<code>.</code>
     */
    if (delim > -1) {
      /**
       * 填充name
       */
      name = fullname.substring(0, delim);
      children = fullname.substring(delim + 1);
    } else {
      /**
       * 不存在<code>.</code> name <= fullname
       */
      name = fullname;
      children = null;
    }
    indexedName = name;
    /**
     * 获取 <code>[</code> 的索引.
     */
    delim = name.indexOf('[');
    if (delim > -1) {
      /**
       * 获取到name中<code>[</code>与结尾<code>]</code>之间的值，作为索引值
       */
      index = name.substring(delim + 1, name.length() - 1);
      /**
       * 去掉 <code>[</code>后的内容为 name
       */
      name = name.substring(0, delim);
    }
  }

  public String getName() {
    return name;
  }

  public String getIndex() {
    return index;
  }

  public String getIndexedName() {
    return indexedName;
  }

  public String getChildren() {
    return children;
  }

  @Override
  public boolean hasNext() {
    return children != null;
  }

  @Override
  public PropertyTokenizer next() {
    return new PropertyTokenizer(children);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException(
        "Remove is not supported, as it has no meaning in the context of properties.");
  }
}
