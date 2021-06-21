package com.jiajun;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * @Author: jiajun
 * @Date: 2021-06-18 9:19
 */
public class MySqlSink extends RichSinkFunction<List<Area>> {
    private PreparedStatement ps;
    private Connection connection;
    private static int index = 2;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        connection = DbUtils.getConnection();
        String sql = "insert into tb_area(ared_id, area_name, priority) values(?, ?, ?);";
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
    public void invoke(List<Area> value, Context context) throws Exception {
        for (Area area : value) {
            ps.setInt(1, area.getAreaId());
            ps.setString(2, area.getAreaName());
            ps.setInt(3, area.getPriority());
        }
        int[] count = ps.executeBatch();
        System.out.println("≥…π¶–¥»Îmysql" + count.length);
    }
}
