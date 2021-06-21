package com.jiajun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bingocloud.ClientConfiguration;
import com.bingocloud.auth.BasicAWSCredentials;
import com.bingocloud.services.s3.AmazonS3;
import com.bingocloud.services.s3.AmazonS3Client;
import com.bingocloud.services.s3.model.S3Object;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.nlpcn.commons.lang.util.IOUtil;

import java.util.Properties;

/**
 * @Author: jiajun
 * @Date: 2021-06-17 11:00
 */
public class Producer {
    //s3 参数

    private final String accessKey = "261EB1C4FC64A6CC7C9D";
    private final String serectKey = "W0YyQjczMTVCNkQ3NEI4RkE0REU5QjE3QjcyQUYw";
    private final String endpoint = "http://scut.depts.bingosoft.net:29997";
    private final String bucket = "chenjiajun";
    private final String key = "data.txt";
    private final String topic = "mn_information_1_chenjiajun2";
    private final String bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";

    // 读取s3数据写入kafka
    public String readFile() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, serectKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        AmazonS3 s3 = new AmazonS3Client(credentials, clientConfiguration);
        s3.setEndpoint(endpoint);
        S3Object object = s3.getObject(bucket, key);
        return IOUtil.getContent(object.getObjectContent(), "UTF-8");
    }

    public void writeToKafka() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("acks", "all");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("request.required.acks","1");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
//        Information information = new Information("类别1","内151");
        String content = readFile();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, content);
        producer.send(record);
        System.out.println("发送数据:"+ content);
        producer.flush();

    }






}
