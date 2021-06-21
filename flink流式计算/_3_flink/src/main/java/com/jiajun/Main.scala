package com.jiajun
import java.sql.{Connection, DriverManager}
import java.util.Properties

import com.bingocloud.{ClientConfiguration, Protocol}
import com.bingocloud.auth.BasicAWSCredentials
import com.bingocloud.services.s3.AmazonS3Client
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.nlpcn.commons.lang.util.IOUtil
/**
  * @Author: jiajun
  * @Date: 2021-06-15 11:24
  */


object Main {
  //s3����
  val accessKey = "261EB1C4FC64A6CC7C9D"
  val secretKey = "W0YyQjczMTVCNkQ3NEI4RkE0REU5QjE3QjcyQUYw"
  val endpoint = "http://scut.depts.bingosoft.net:29997"
  val bucket = "chenjiajun"
  //Ҫ��ȡ���ļ�
  val key = "demo.txt"

  //kafka����
  val topic = "mn_buy_ticket_1_chenjiajun"
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val s3Content = readMysql()
    produceToKafka(s3Content)
  }

  /**
    * ��s3�ж�ȡ�ļ�����
    *
    * @return s3���ļ�����
    */
  def readFile(): String = {
    val credentials = new BasicAWSCredentials(accessKey, secretKey)
    val clientConfig = new ClientConfiguration()
    clientConfig.setProtocol(Protocol.HTTP)
    val amazonS3 = new AmazonS3Client(credentials, clientConfig)
    amazonS3.setEndpoint(endpoint)
    val s3Object = amazonS3.getObject(bucket, key)
    IOUtil.getContent(s3Object.getObjectContent, "UTF-8")
  }

  /**
    * ������д�뵽kafka��
    *
    * @param s3Content Ҫд�������
    */
  def produceToKafka(s3Content: String): Unit = {
    val props = new Properties
    props.put("bootstrap.servers", bootstrapServers)
    props.put("acks", "all")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)

    val dataArr = s3Content.split("\n")
    for (s <- dataArr) {
      if (!s.trim.isEmpty) {
        val record = new ProducerRecord[String, String](topic, null, s)
        println("��ʼ�������ݣ�" + s)
        producer.send(record)
      }
    }
    producer.flush()
    producer.close()
  }

  def readMysql():String ={
    val username = "root"
    val password = "295747"
    val drive = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306/o2o"
    var connection:Connection = null
    try {
      classOf[com.mysql.jdbc.Driver]
      connection = DriverManager.getConnection(url,username,password)
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("select * from tb_area")

      while (resultSet.next()) {
        return resultSet.getString("area_name")
      }
      return null;

    }

  }
}

