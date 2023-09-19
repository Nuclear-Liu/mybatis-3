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
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类型处理器接口.
 *
 * @author Clinton Begin
 */
public interface TypeHandler<T> {

  /**
   * 将 Java 类型转换为 JDBC 类型.
   * <p/>
   * 操作的本质：占位赋值 <code>
   *   String sql = "select id, user_name, real_name, password from t_user where id = ? and user_name = ?";
   *   ps = conn.prepareStatement(sql);
   *   ps.setInt(1,2);
   *   ps.setString(2, "zhangsan");
   * </code>
   *
   * @param ps
   *          {@link PreparedStatement} 对象
   * @param i
   *          占位符位置
   * @param parameter
   *          参数值
   * @param jdbcType
   *          对应 JDBC 类型
   *
   * @throws SQLException
   */
  void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

  /**
   * Gets the result.
   * <p/>
   * 将 JDBC 类型转换为 Java 类型.
   *
   * @param rs
   *          the rs
   * @param columnName
   *          Column name, when configuration <code>useColumnLabel</code> is <code>false</code>
   *
   * @return the result
   *
   * @throws SQLException
   *           the SQL exception
   */
  T getResult(ResultSet rs, String columnName) throws SQLException;

  T getResult(ResultSet rs, int columnIndex) throws SQLException;

  T getResult(CallableStatement cs, int columnIndex) throws SQLException;

}
