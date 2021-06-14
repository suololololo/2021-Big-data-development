package com.jiajun.service.impl;

import com.jiajun.SparkSql_hive;
import com.jiajun.entity.Database;
import com.jiajun.result.Result;
import com.jiajun.service.DatabaseService;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;

/**
 * @Author: jiajun
 * @Date: 2021-06-09 14:35
 */
@Service
public class DatabaseServiceImpl implements DatabaseService {
    @Override
    public ResultSet listDatabase() {
        return SparkSql_hive.listDatabase("show databases");

    }

    @Override
    public ResultSet listTables(String databaseName) {
        return SparkSql_hive.listTables(databaseName);

    }

    @Override
    public ResultSet listData(String databaseName, String tableName) {
        return SparkSql_hive.listData(databaseName, tableName);
    }

    @Override
    public ResultSet listTupleDesc(String databaseName, String tableName) {
        return null;
    }

    @Override
    public ResultSet executeSql(String databaseName, String sql) {
        return SparkSql_hive.executeSql(databaseName,sql);
    }

    @Override
    public ResultSet conect(String connectionName, String ip, String port, String userName, String password) {
        return SparkSql_hive.connect(connectionName, ip, port, userName, password);
    }

}
