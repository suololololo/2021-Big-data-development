package com.jiajun;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.compress.utils.Lists;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * @Author: jiajun
 * @Date: 2021-06-17 17:00
 */
public class Run {
    public static void main(String[] args) throws Exception{
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        int period = 5000;
        //输入的kafka主题名称
        String inputTopic = "mn_buy_ticket_1_chenjiajun1";
        //kafka地址
        String bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
        Properties kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", bootstrapServers);
        kafkaProperties.put("group.id", UUID.randomUUID().toString());
        kafkaProperties.put("auto.offset.reset", "earliest");
        kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        DataStreamSource<String> dataStreamSource = env.addSource(new FlinkKafkaConsumer010<String>(inputTopic,new SimpleStringSchema(),kafkaProperties))
                .setParallelism(1);
        DataStream<Area> dataStream = dataStreamSource.map(value -> JSONObject.parseObject(value,Area.class));
        dataStream.timeWindowAll(Time.seconds(5L)).apply(new AllWindowFunction<Area, List<Area>, TimeWindow>() {
            @Override
            public void apply(TimeWindow timeWindow, Iterable<Area> iterable, Collector<List<Area>> collector) throws Exception {
                List<Area> areas = new ArrayList<>();
                iterable.forEach(single -> {
                    areas.add(single);
                });
                if (areas.size() > 0) {
                    System.out.println("5秒的总共收到的条数:" + areas.size());
                    collector.collect(areas);
                }
            }
        }).addSink(new MySqlSink());

        env.execute("flink learning connectors kafka");
    }
}
