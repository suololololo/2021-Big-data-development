package com.jiajun

/**
  * @Author: jiajun
  * @Date: 2021-06-15 14:14
  */
import java.util
import java.util.{Properties, UUID}
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema

object Main {
  //��Ҫ��ص�����
  val user = "������"
  val inputTopics: util.ArrayList[String] = new util.ArrayList[String]() {
    {
      add("mn_buy_ticket_1") //��Ʊ�����¼����
      add("mn_hotel_stay_1") //�Ƶ���ס��Ϣ����
      add("mn_monitoring_1") //���ϵͳ��������
    }
  }
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

    val text = env.socketTextStream("localhost", 9999)

    val kafkaConsumer = new FlinkKafkaConsumer010[ObjectNode](inputTopics,
          new JSONKeyValueDeserializationSchema(true), kafkaProperties)
        kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
        val inputKafkaStream = env.addSource(kafkaConsumer)
        inputKafkaStream.filter(x => x.get("value").get("username").asText("").equals(text.name)).map(x => {
          (x.get("metadata").get("topic").asText("") match {
            case "mn_monitoring_1"
            => x.get("value").get("found_time")
            case _ => x.get("value").get("buy_time")
          }, x)
        }).print()
    env.execute()
  }
}

