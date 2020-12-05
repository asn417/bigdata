package com.asn.producer;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

/**
 * @Author: wangsen
 * @Date: 2020/12/5 18:05
 * @Description: kafka生产者工具类
 **/
public class ProducerUtil {
    private static KafkaProducer<String, String> producer;
    private static Properties kafkaProps = new Properties();
    static {
        kafkaProps.put("bootstrap.servers", "flink1:9092,flink2:9092,flink3:9092");
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    }
    public static KafkaProducer<String, String> getInstance(){
        if (producer == null){
            synchronized (ProducerUtil.class) {
                if (producer == null) {
                    producer = new KafkaProducer(kafkaProps);
                }
            }
        }
        return producer;
    }
}
