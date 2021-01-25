package com.asn.learn.window;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public class TimeWindowDemo {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"flink1:9092,flink2:9092,flink3:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"test-group");

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<String>("test-topic",new SimpleStringSchema(),properties);

        consumer.setStartFromGroupOffsets();

        DataStreamSource<String> source = env.addSource(consumer);

    }
}
