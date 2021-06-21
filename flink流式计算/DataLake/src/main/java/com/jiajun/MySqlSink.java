package com.jiajun;

/**
 * @Author: jiajun
 * @Date: 2021-06-18 11:16
 */

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;



public class MySqlSink extends RichSinkFunction<Information> {
    private PreparedStatement ps;
    private Connection connection;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        connection = DbUtils.getConnection();
        String sql = "insert into information(key, content) values(?, ?);";
        ps = connection.prepareStatement(sql);
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (connection != null) {
            connection.close();
        }
        if (ps != null) {
            ps.close();
        }
    }

    @Override
    public void invoke(Information value, Context context) throws Exception {
        ps.setString(1, value.getKey());
        ps.setString(2, value.getContent());
        ps.executeUpdate();
        System.out.println("≥…π¶–¥»Îmysql");
    }
}
