package com.jiajun;

/**
 * @Author: jiajun
 * @Date: 2021-06-17 16:45
 */

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Desc: ��kafka��д����,����ʹ�����main�������в���
 * Created by zhisheng on 2019-02-17
 * Blog: http://www.54tianzhisheng.cn/tags/Flink/
 */
public class KafkaUtil {
    public static final String broker_list = "localhost:9092";
    public static final String topic = "student";  //kafka topic ��Ҫ�� flink ������ͬһ�� topic

    public static void writeToKafka() throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", broker_list);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(props);

        for (int i = 2; i <= 100; i++) {
            Area student = new Area(i, "zhisheng" + i, i);
            ProducerRecord record = new ProducerRecord<String, String>(topic, null, null, JSONObject.toJSONString(student));
            producer.send(record);
            System.out.println("��������: " +JSONObject.toJSONString(student));
            Thread.sleep(10 * 1000); //����һ������ sleep 10s���൱�� 1 ���� 6 ��
        }
        producer.flush();
    }

    public static void main(String[] args) throws InterruptedException {
        writeToKafka();
    }
}