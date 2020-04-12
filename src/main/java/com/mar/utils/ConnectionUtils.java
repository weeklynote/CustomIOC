package com.mar.utils;


import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author: 刘劲
 * @Date: 2020/4/7 23:10
 */
public class ConnectionUtils {


    private ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    public Connection getCurrentConnection() throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if (connection == null){
            connection = DruidUtils.getInstance().getConnection();
            connectionThreadLocal.set(connection);
        }
        return connection;
    }
}
