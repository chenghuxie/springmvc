package com.xch.test;


import com.xch.connection.ConnectionPoolManager;

import java.sql.Connection;

/**
 * @author xiech
 * @create 2020-01-21 11:12
 */
public class Test1 {
    public static void main(String[] args) {
       ThreadConnection threadConnection=new ThreadConnection();
       for (int i=0;i<10;i++){
           Thread t=new Thread(threadConnection);
           t.start();
        }
    }
}

class ThreadConnection implements Runnable{

    @Override
    public void run() {
        Connection connection=ConnectionPoolManager.getConnection();
        System.out.println(Thread.currentThread().getName()+",connection"+connection);
        ConnectionPoolManager.raleaseConnection(connection);
    }
}
