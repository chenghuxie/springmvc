package com.xch.connection;

import com.xch.bean.DbBean;

import java.sql.Connection;

/**
 * @author xiech
 * @create 2020-01-21 11:09
 */
public class ConnectionPoolManager {
    private static DbBean dbBean=new DbBean();
    private static ConnectionPool connectionPool=new ConnectionPool(dbBean);

    public static Connection getConnection(){
        return connectionPool.getConnection();
    }

    public static void raleaseConnection(Connection connection){
        connectionPool.raleaseConnection(connection);
    }
}
