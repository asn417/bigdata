package com.asn.learn.source.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public class ReadKafka {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        environment.setParallelism(4);
        environment.enableCheckpointing(5000);//5秒进行一次checkpoint，便于失败容错
        environment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);//设置检查语义位exactly_once
        //表示任务cancel后保留CheckPoint数据，以便恢复
        environment.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"master:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"test-group");

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<String>("test-topic",new SimpleStringSchema(),properties);
        consumer.setStartFromEarliest();
        //consumer.setStartFromGroupOffsets();

        DataStreamSource<String> source = environment.addSource(consumer);

        source.print().setParallelism(1);

        environment.execute("consumer011-->");
    }
}

