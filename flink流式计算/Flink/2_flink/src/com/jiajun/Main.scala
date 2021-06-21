package com.jiajun
import java.util.{Properties, UUID}


import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010

/**
  * @Author: jiajun
  * @Date: 2021-06-15 11:06
  */

object Main {
  /**
    * �������������
    */
  val inputTopic = "mn_buy_ticket_1"
  /**
    * kafka��ַ
    */
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
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

//    inputKafkaStream.map((_,1)).keyBy(0).sum(1).print().setParallelism(1)
    inputKafkaStream.flatMap(_.split(",")).filter(_.contains("destination"))
      .map((_,1))
      .keyBy(0)
      .sum(1).keyBy(0).max(1)
      .print().setParallelism(1)


    env.execute()

  }
}
