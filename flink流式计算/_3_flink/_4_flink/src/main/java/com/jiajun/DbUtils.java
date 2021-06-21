package com.jiajun;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;

/**
 * @Author: jiajun
 * @Date: 2021-06-18 9:11
 */
public class DbUtils {
    private static DruidDataSource dataSource;
    public static Connection getConnection() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql//localhost:3306/o2o");
        dataSource.setUsername("root");
        dataSource.setPassword("295747");
        return dataSource.getConnection();
    }
}
