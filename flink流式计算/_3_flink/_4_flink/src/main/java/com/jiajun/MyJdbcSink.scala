package com.jiajun

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

/**
  * @Author: jiajun
  * @Date: 2021-06-17 16:07
  */
class MyJdbcSink {
//  var conn: Connection = _
//  var insertStmt: PreparedStatement = _
//  var updateStmt: PreparedStatement = _
//
//  override def open(parameters: Configuration): Unit = {
//    super.open(parameters)
//    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/o2o", "root", "295747")
//    insertStmt = conn.prepareStatement("insert into tb_area (area_name.priority) values(?,?)")
//  }
//
//  override def invoke(value: Any, context: SinkFunction.Context[_]): Unit = {
//    super.invoke(value, context)
//    insertStmt.setString(1,value.area_name)
//    insertStmt.setInt(2,value.priority)
//    insertStmt.execute()
//  }
//
//  override def close(): Unit = {
//    super.close()
//    insertStmt.close()
//    conn.close()
//  }

}
