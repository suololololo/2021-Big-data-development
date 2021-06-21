package com.jiajun
import java.util.{Properties, UUID}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010}
/**
  * @Author: jiajun
  * @Date: 2021-06-15 11:51
  */


object Main {
  val accessKey = "261EB1C4FC64A6CC7C9D"
  val secretKey = "W0YyQjczMTVCNkQ3NEI4RkE0REU5QjE3QjcyQUYw"
  //s3��ַ
  val endpoint = "http://scut.depts.bingosoft.net:29997"
  //�ϴ�����Ͱ
  val bucket = "chenjiajun"
  //�ϴ��ļ���·��ǰ׺
  val keyPrefix = "upload/"
  //�ϴ����ݼ�� ��λ����
  val period = 5000
  //�����kafka��������
  val inputTopic = "mn_buy_ticket_1_chenjiajun"
  //kafka��ַ
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[String](inputTopic,
      new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)
    inputKafkaStream.writeUsingOutputFormat(new S3Writer(accessKey, secretKey, endpoint, bucket, keyPrefix, period))
    env.execute()
  }
}
