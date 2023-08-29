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
package org.apache.ibatis.transaction.jdbc;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

/**
 * Creates {@link JdbcTransaction} instances.
 * <p/>
 * 用于创建 {@link JdbcTransaction} 实例的工厂.
 *
 * @author Clinton Begin
 *
 * @see JdbcTransaction
 */
public class JdbcTransactionFactory implements TransactionFactory {

  private boolean skipSetAutoCommitOnClose;

  /**
   * {@inheritDoc}
   *
   * @param props
   *          the new properties
   */
  @Override
  public void setProperties(Properties props) {
    if (props == null) {
      return;
    }
    String value = props.getProperty("skipSetAutoCommitOnClose");
    if (value != null) {
      skipSetAutoCommitOnClose = Boolean.parseBoolean(value);
    }
  }

  /**
   * {@inheritDoc}
   * <p/>
   * 创建 {@link JdbcTransaction} 对象
   *
   * @param conn
   *          Existing database connection
   *
   * @return {@link JdbcTransaction}
   */
  @Override
  public Transaction newTransaction(Connection conn) {
    return new JdbcTransaction(conn);
  }

  /**
   * {@inheritDoc}
   *
   * @param ds
   *          DataSource to take the connection from
   * @param level
   *          Desired isolation level
   * @param autoCommit
   *          Desired autocommit
   *
   * @return
   */
  @Override
  public Transaction newTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommit) {
    return new JdbcTransaction(ds, level, autoCommit, skipSetAutoCommitOnClose);
  }
}
