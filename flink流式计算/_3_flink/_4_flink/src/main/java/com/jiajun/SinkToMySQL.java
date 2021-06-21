package com.jiajun;

/**
 * @Author: jiajun
 * @Date: 2021-06-17 16:51
 */

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Desc: �������� sink ���ݵ� mysql
 * Created by zhisheng_tian on 2019-02-17
 * Blog: http://www.54tianzhisheng.cn/tags/Flink/
 */
public class SinkToMySQL extends RichSinkFunction<List<Area>> {
    PreparedStatement ps;
    BasicDataSource dataSource;
    private Connection connection;

    /**
     * open() �����н������ӣ���������ÿ�� invoke ��ʱ��Ҫ�������Ӻ��ͷ�����
     *
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        dataSource = new BasicDataSource();
        connection = getConnection(dataSource);
        String sql = "insert into tb_area(area_id, area_name, priority) values(?, ?, ?);";
        ps = this.connection.prepareStatement(sql);
    }

    @Override
    public void close() throws Exception {
        super.close();
        //�ر����Ӻ��ͷ���Դ
        if (connection != null) {
            connection.close();
        }
        if (ps != null) {
            ps.close();
        }
    }

    /**
     * ÿ�����ݵĲ��붼Ҫ����һ�� invoke() ����
     *
     * @param value
     * @param context
     * @throws Exception
     */
    @Override
    public void invoke(List<Area> value, Context context) throws Exception {
        //�������ݼ���
        for (Area area : value) {
            ps.setInt(1, area.getAreaId());
            ps.setString(2, area.getAreaName());
            ps.setInt(3, area.getPriority());
            ps.addBatch();
        }
        int[] count = ps.executeBatch();//������ִ��
        System.out.println("�ɹ��˲�����" + count.length + "������");
    }


    private static Connection getConnection(BasicDataSource dataSource) {
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        //ע�⣬�滻���Լ����ص� mysql ���ݿ��ַ���û���������
        dataSource.setUrl("jdbc:mysql://localhost:3306/o2o");
        dataSource.setUsername("root");
        dataSource.setPassword("295747");
        //�������ӳص�һЩ����
        dataSource.setInitialSize(10);
        dataSource.setMaxTotal(50);
        dataSource.setMinIdle(2);

        Connection con = null;
        try {
            con = dataSource.getConnection();
            System.out.println("�������ӳأ�" + con);
        } catch (Exception e) {
            System.out.println("-----------mysql get connection has exception , msg = " + e.getMessage());
        }
        return con;
    }
}