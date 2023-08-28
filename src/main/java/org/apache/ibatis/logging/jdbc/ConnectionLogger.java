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
package org.apache.ibatis.logging.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.reflection.ExceptionUtil;

/**
 * Connection proxy to add logging.
 * <p/>
 * 连接代理添加日志记录.
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public final class ConnectionLogger extends BaseJdbcLogger implements InvocationHandler {

  /**
   * 被代理的数据库连接器对象.
   */
  private final Connection connection;

  private ConnectionLogger(Connection conn, Log statementLog, int queryStack) {
    super(statementLog, queryStack);
    this.connection = conn;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
    try {
      /**
       * 如果调用的是 {@link Object} 继承的方法，直接调用(equals/hashCode/toString...).
       */
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, params);
      }
      /**
       * 如果调用的方法名称为: prepareStatement 或 prepareCall
       */
      if ("prepareStatement".equals(method.getName()) || "prepareCall".equals(method.getName())) {
        if (isDebugEnabled()) {
          // 输出日志
          debug(" Preparing: " + removeExtraWhitespace((String) params[0]), true);
        }
        /**
         * 创建 {@link PreparedStatement} 对象
         */
        PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
        /**
         * 创建 {@link PreparedStatement} 的代理对象
         */
        return PreparedStatementLogger.newInstance(stmt, statementLog, queryStack);
      }
      if ("createStatement".equals(method.getName())) {
        /**
         * 创建 {@link Statement} 对象.
         */
        Statement stmt = (Statement) method.invoke(connection, params);
        /**
         * 创建 {@link Statement} 的代理对象
         */
        return StatementLogger.newInstance(stmt, statementLog, queryStack);
      } else {
        return method.invoke(connection, params);
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
  }

  /**
   * Creates a logging version of a connection.
   * <p/>
   * 创建数据库连接器对象的日志代理对象
   *
   * @param conn
   *          the original connection
   * @param statementLog
   *          the statement log
   * @param queryStack
   *          the query stack
   *
   * @return the connection with logging
   */
  public static Connection newInstance(Connection conn, Log statementLog, int queryStack) {
    InvocationHandler handler = new ConnectionLogger(conn, statementLog, queryStack);
    ClassLoader cl = Connection.class.getClassLoader();
    return (Connection) Proxy.newProxyInstance(cl, new Class[] { Connection.class }, handler);
  }

  /**
   * return the wrapped connection.
   *
   * @return the connection
   */
  public Connection getConnection() {
    return connection;
  }

}
