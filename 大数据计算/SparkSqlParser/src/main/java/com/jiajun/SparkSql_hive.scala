package com.jiajun

/**
  * @Author: jiajun
  * @Date: 2021-06-09 11:53
  */

import java.sql.ResultSet
import java.util.Properties

import com.jiajun.entity.Tuple
import com.jiajun.result.Result

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object SparkSql_hive {

  class Config {
    var connectionName: String = _
    var ip: String = _
    var port: String = _
    var userName: String = _
    var password: String = _
    var properties = new Properties()
    var url: String = _
  }

  var config = new Config()

  def listTables(databaseName: String): ResultSet = {
    import java.sql.DriverManager
    val url = config.url + databaseName

    val connection = DriverManager.getConnection(url, config.properties)
    val statement = connection.createStatement
    var resultSet = statement.executeQuery("show tables")
    return resultSet
  }

  def main(args: Array[String]): Unit = {
    import java.sql.DriverManager
    //    val url = "jdbc:hive2://bigdata107.depts.bingosoft.net:22107/user02_db"
    val url = "jdbc:hive2://10.16.1.107:10009/user02_db"
    val properties = new Properties()
    properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver")
    properties.setProperty("user", "user02")
    properties.setProperty("password", "pass@bingo2")

    val connection = DriverManager.getConnection(url, properties)

    val statement = connection.createStatement

    var resultSet = statement.executeQuery("show tables")

    val a = resultSet.getRow()
    val meta = resultSet.getMetaData()
    // 获取列数
    val size = meta.getColumnCount()
    val tupleDesc =
      try {
        while (resultSet.next) {
          resultSet.getRow()
          val tableName = resultSet.getString(1)

          //输出所有表名
          println("tableName：" + tableName)
        }
        resultSet.close()

        //查询t_rk_jbxx表结构
        resultSet = statement.executeQuery("desc formatted t_rk_jbxx")
        while (resultSet.next) {
          val columnName = resultSet.getString(1)
          val columnType = resultSet.getString(2)
          val comment = resultSet.getString(3)
          //输出表结构
          println(s"columnName：$columnName     columnType:$columnType     comment:$comment")
        }
        resultSet.close()

        //查询t_rk_jbxx表数据
        println("表数据：")
        resultSet = statement.executeQuery("select * from t_rk_jbxx limit 10")
        while (resultSet.next) {
          val asjbh = resultSet.getString(4)
          val ajmc = resultSet.getString(1)
          val bamjbh = resultSet.getString(8)
          //输出表数据

          println(s"$asjbh    ｜$ajmc    ｜$bamjbh")
        }
        resultSet.close()
      } catch {
        case e: Exception => e.printStackTrace()
      }
  }

  def listDatabase(sql: String): ResultSet = {
    import java.sql.DriverManager

    val connection = DriverManager.getConnection(config.url, config.properties)
    val statement = connection.createStatement
    var resultSet = statement.executeQuery(sql)

    return resultSet
  }

  def listData(databaseName: String, tableName: String): ResultSet = {
    import java.sql.DriverManager
    val url = config.url + databaseName

    val connection = DriverManager.getConnection(url, config.properties)
    val statement = connection.createStatement
    var resultSet = statement.executeQuery("select * from" + "  " + tableName + "  " + "limit 100")
    return resultSet
  }

  def listTupleDesc(databaseName: String, tableName: String): ResultSet = {
    import java.sql.DriverManager
    val url = config.url + databaseName

    val connection = DriverManager.getConnection(url, config.properties)
    val statement = connection.createStatement
    var resultSet = statement.executeQuery("desc formatted " + " " + tableName)
    return resultSet
  }


  def executeSql(databaseName: String, sql: String): ResultSet = {
    import java.sql.DriverManager
    val url = config.url + databaseName

    val connection = DriverManager.getConnection(url, config.properties)
    val statement = connection.createStatement
    var resultSet = statement.executeQuery(sql)
    return resultSet
  }


  def connect(connectionName: String, ip: String, port: String, userName: String, password: String): ResultSet = {
    config.connectionName = connectionName
    config.ip = ip
    config.port = port
    config.userName = userName
    config.password = password
    config.properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver")
    config.properties.setProperty("user", userName)
    config.properties.setProperty("password", password)
    import java.sql.DriverManager
    //    val url = "jdbc:hive2://10.16.1.107:10009/"
    //    var url = "jdbc:hive2://" + ip + ":" + port + "/"
    config.url = "jdbc:hive2://" + ip + ":" + port + "/"
    val connection = DriverManager.getConnection(config.url, config.properties)
    val statement = connection.createStatement
    var resultSet = statement.executeQuery("show databases");
    return resultSet
  }

}
