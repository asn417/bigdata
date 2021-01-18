package com.asn.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * @Author: wangsen
 * @Date: 2021/1/18 16:39
 * @Description: 消费者
 *  1、自动提交offset：需要配置两个参数（enable.auto.commit，auto.commit.interval.ms）
 *  2、手动提交offset：手动提交需要关闭自动提交enable.auto.commit=false。手动提交有两种，异步和同步，这两种都只能提交到kafka。
 *  3、自定义提交offset存储到外部存储系统。将消费消息和提交offset放到一个事务中，实现exactly once。
 **/
public class ConsumerDemo {
    private static Properties kafkaProps = new Properties();
    static {
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "flink1:9092,flink2:9092,flink3:9092");
        kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "com.asn.serializer.MessageSerializer");
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG,"group_id");
        kafkaProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");//关闭自动提交,使用手动提交
        //kafkaProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");//提交间隔
        kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
    }
    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);

        consumer.subscribe(Arrays.asList("topic1"));
        while (true){
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord record:consumerRecords) {
                System.out.println("topic:"+record.topic()+",offset:"+record.offset()+",value:"+record.value());
            }
            //异步提交到kafka，还可以指定回调函数，或分区offset
            consumer.commitAsync();
            //同步提交到kafka
            consumer.commitSync();
        }

    }
}
