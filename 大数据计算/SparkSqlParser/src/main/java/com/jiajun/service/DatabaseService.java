package com.jiajun.service;

import com.jiajun.entity.Database;
import com.jiajun.result.Result;
import org.springframework.stereotype.Service;


import java.sql.ResultSet;
import java.util.List;

/**
 * @Author: jiajun
 * @Date: 2021-06-09 14:21
 */

public interface DatabaseService {
    ResultSet listDatabase();

    ResultSet listTables(String databaseName);

    ResultSet listData(String databaseName, String tableName);

    ResultSet listTupleDesc(String databaseName, String tableName);

    ResultSet executeSql(String databaseName, String sql);

    ResultSet conect(String connectionName, String ip, String port, String userName, String password);
}
