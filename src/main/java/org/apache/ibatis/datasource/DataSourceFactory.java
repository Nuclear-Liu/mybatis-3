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
package org.apache.ibatis.datasource;

import java.util.Properties;

import javax.sql.DataSource;

/**
 * @author Clinton Begin
 */
public interface DataSourceFactory {

  /**
   * 在全局配置文件加载过程中 <code>environments.environment.dataSource</code> 标签中的内容初始化到当前方法中.
   *
   * @param props
   *          {@link Properties} 配置属性
   */
  void setProperties(Properties props);

  /**
   * 获取数据源实例 {@link DataSource}.
   *
   * @return
   */
  DataSource getDataSource();

}
