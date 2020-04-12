package com.mar.utils;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @Author: 刘劲
 * @Date: 2020/4/7 22:55
 */
public class DruidUtils {

    private DruidUtils(){}

    private static DruidDataSource dataSource = new DruidDataSource();

    static {
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/note");
        dataSource.setUsername("root");
        dataSource.setPassword("lj123456");
    }

    public static DruidDataSource getInstance(){
        return dataSource;
    }
}
