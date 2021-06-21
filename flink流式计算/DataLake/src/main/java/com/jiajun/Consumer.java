package com.jiajun;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.scala.function.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.nlpcn.commons.lang.util.tuples.Tuple;

import java.io.Serializable;
import java.util.*;

import static java.awt.SystemColor.window;

/**
 * @Author: jiajun
 * @Date: 2021-06-17 11:01
 */
public class Consumer {
    private String accessKey = "261EB1C4FC64A6CC7C9D";
    private String secretKey = "W0YyQjczMTVCNkQ3NEI4RkE0REU5QjE3QjcyQUYw";
    //s3地址
    private String endpoint = "http://scut.depts.bingosoft.net:29997";
    //上传到的桶
    private String bucket = "chenjiajun";
    //上传文件的路径前缀
    private String keyPrefix = "upload/";
    //上传数据间隔 单位毫秒
    private int period = 5000;
    private final String inputTopic = "mn_information_1_chenjiajun2";
    private final String bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";

    public void consume() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
//        properties.put("group.id", UUID.randomUUID().toString());
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        DataStream<String> stream = env.addSource(new FlinkKafkaConsumer010<String>(inputTopic,new SimpleStringSchema(), properties));
        DataStream<String> dataStream = stream.keyBy("key");
        dataStream.writeUsingOutputFormat(new S3Writer(accessKey, secretKey, endpoint, bucket, keyPrefix, period));
//        SingleOutputStreamOperator<Information> dataStreamSource = env.addSource(
//                new FlinkKafkaConsumer010<>(inputTopic, new SimpleStringSchema(), properties)
//        ).map(string -> JSONObject.parseObject(string, Information.class)).setParallelism(1);
//        dataStreamSource.print();
//        dataStreamSource.addSink(new MySqlSink());
//        DataStream<Information> dataStream = dataStreamSource.map(value -> JSONObject.parseObject(value, Information.class));

//        dataStream.timeWindowAll(Time.hours(5L)).apply(new AllWindowFunction<Information, Information, TimeWindow>() {
//            @Override
//            public void apply(TimeWindow timeWindow, Iterable<Information> iterable, Collector<Information> collector) throws Exception {
//                iterable.forEach(x -> {
//                    collector.collect(x);
//                    System.out.println("接收信息");
//                });
//            }
//        }).addSink(new MySqlSink());

        env.execute();

    }
}