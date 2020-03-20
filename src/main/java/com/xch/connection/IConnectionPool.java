package com.xch.connection;

import java.sql.Connection;

/**
 * @author xiech
 * @create 2020-01-21 9:37
 */
public interface IConnectionPool {

    public Connection getConnection();

    public void raleaseConnection(Connection connection);
}
