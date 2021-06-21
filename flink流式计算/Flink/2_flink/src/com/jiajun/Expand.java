package com.jiajun;


import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;
import java.util.UUID;

/**
 * @Author: jiajun
 * @Date: 2021-06-19 10:53
 */
public class Expand {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
        String inputTopic = "mn_buy_ticket_1";
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("group.id", UUID.randomUUID().toString());
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        DataStreamSource<String> dataStreamSource =  env.addSource(new FlinkKafkaConsumer010<String>(inputTopic,new SimpleStringSchema(),properties));
        DataStream<Bean> dataStream = dataStreamSource.map(new MapFunction<String, Bean>() {
            @Override
            public Bean map(String s) throws Exception {
                String[] res = s.split(",");
                for(String str: res) {
                    if (str.startsWith("destianation")) {
                        return new Bean(str.substring(str.indexOf(":")+1));
                    }

                }
                return null;
            }
        });
        KeyedStream<Bean, String> keyedStream = dataStream.keyBy(data -> data.getDestination());
//        DataStream<String> temp = keyedStream.sum();
        keyedStream.print();

        env.execute();
    }
}
