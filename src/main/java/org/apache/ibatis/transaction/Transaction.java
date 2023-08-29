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
package org.apache.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wraps a database connection. Handles the connection lifecycle that comprises: its creation, preparation,
 * commit/rollback and close.
 * <p/>
 * 封装数据库连接，提供事务管理能力（创建、准备、提交、回滚和关闭）.
 *
 * @author Clinton Begin
 */
public interface Transaction {

  /**
   * Retrieve inner database connection.
   * <p/>
   * 获取数据库连接.
   *
   * @return DataBase connection
   *
   * @throws SQLException
   *           the SQL exception
   */
  Connection getConnection() throws SQLException;

  /**
   * Commit inner database connection.
   * <p/>
   * 提交事务
   *
   * @throws SQLException
   *           the SQL exception
   */
  void commit() throws SQLException;

  /**
   * Rollback inner database connection.
   * <p/>
   * 事务回滚.
   *
   * @throws SQLException
   *           the SQL exception
   */
  void rollback() throws SQLException;

  /**
   * Close inner database connection.
   * <p/>
   * 关闭数据库连接.
   *
   * @throws SQLException
   *           the SQL exception
   */
  void close() throws SQLException;

  /**
   * Get transaction timeout if set.
   * <p/>
   * 获取事务超时时间（如果设置）.
   *
   * @return the timeout
   *
   * @throws SQLException
   *           the SQL exception
   */
  Integer getTimeout() throws SQLException;

}
