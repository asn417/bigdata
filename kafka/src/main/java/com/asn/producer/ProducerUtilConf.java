package com.asn.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author: wangsen
 * @Date: 2020/12/5 18:05
 * @Description: 注解方式实现的kafka生产者工具类
 **/
@Configuration
@ConfigurationProperties("kafka.producer")
public class ProducerUtilConf {
    @Value("${kafka.producer.bootstrap.servers}")
    private String servers;
    @Value("${kafka.producer.key.serializer}")
    private String keySerializer;
    @Value("${kafka.producer.value.serializer}")
    private String valueSerializer;

    private static KafkaProducer<String, String> producer;
    private static Properties kafkaProps = new Properties();

    public KafkaProducer<String, String> getInstance(){
        if (producer == null){
            synchronized (ProducerUtilConf.class) {
                if (producer == null) {
                    kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,servers);
                    kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,keySerializer);
                    kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,valueSerializer);
                    producer = new KafkaProducer(kafkaProps);
                }
            }
        }
        return producer;
    }

    public String getServers() {
        return servers;
    }
}
