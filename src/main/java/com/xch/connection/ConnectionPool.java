package com.xch.connection;

import com.xch.bean.DbBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 手写数据库连接池
 * @author xiech
 * @create 2020-01-21 9:38
 */
public class ConnectionPool implements IConnectionPool {
    private DbBean dbBean;

    //空闲连接池
    private List<Connection> freeConnectionPool=new Vector<>();
    //活动连接池
    private List<Connection> activeConnectionPool=new Vector<>();

    //统计中的线程数
    private AtomicInteger countConnection=new AtomicInteger(0);


    public ConnectionPool(DbBean dbBean){
        this.dbBean=dbBean;
        try {
            //初始化线程池
            initDb();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    //初始化线程池
    private void initDb() throws Exception {
        if(dbBean==null){
            throw new Exception("请提供线程池参数");
        }
        for (int i=0;i<dbBean.getInitConnections();i++){
            Connection connection=newConnection();
        }

    }

    private synchronized Connection newConnection() {
        try {
            Class.forName(dbBean.getDriverName());
            Connection connection = DriverManager.getConnection(dbBean.getUrl(), dbBean.getUserName(), dbBean.getPassword());
             countConnection.incrementAndGet();
            return connection;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取连接
     * @return
     */
    @Override
    public synchronized Connection getConnection() {
        Connection connection=null;
        try {
            //判断目前线程数是否超过最大线程数
            if(countConnection.get()<dbBean.getMaxActiveConnections()){
              //判断空闲线程是否有连接
                if(freeConnectionPool.size()>0){
                    //取出一个连接
                    connection=freeConnectionPool.remove(0);
                }else{
                    connection=newConnection();
                }
                //判断连接是否可用
                boolean isAvailable =isAvailable(connection);
                if(isAvailable){
                    //可用  加到活动线程中
                    activeConnectionPool.add(connection);
                }else{
                    countConnection.decrementAndGet();
                    connection=getConnection();
                }
            }else{
                //超过最大线程数 等待
                wait(dbBean.getConnTimeOut());
                //重试
                connection=getConnection();
            }
            return connection;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 销毁连接
     * @param connection
     */
    @Override
    public synchronized void raleaseConnection(Connection connection) {
    //思想 ：将线程放回空闲线程

        try {
            //判断线程是否可用
            boolean available = isAvailable(connection);
            if(available){
                //可用 判断空闲线程是否已满
                if(freeConnectionPool.size()<=dbBean.getMaxConnections()){
                     freeConnectionPool.add(connection);
                }else{
                    connection.close();
                }
                activeConnectionPool.remove(connection);
                countConnection.decrementAndGet();
                notifyAll();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean isAvailable(Connection connection) throws SQLException {
        if(connection==null|| connection.isClosed()){
            return false;
        }
        return true;
    }
}
