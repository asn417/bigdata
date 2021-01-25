package com.asn.producer;

import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Author: wangsen
 * @Date: 2020/12/5 18:05
 * @Description: kafka生产者工具类
 **/
public class ProducerUtil {
    private static Logger logger = LoggerFactory.getLogger(ProducerUtil.class);

    private static KafkaProducer<String, String> producer;
    private static Properties kafkaProps = new Properties();
    static {
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "master:9092");
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //Set acknowledgements for producer requests.
        kafkaProps.put(ProducerConfig.ACKS_CONFIG, "all");
        //If the request fails, the producer can automatically retry,
        kafkaProps.put(ProducerConfig.RETRIES_CONFIG, 0);
        //数据达到batch.size才会发送
        kafkaProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        //发送的时间间隔，linger.ms和batch.size只要有一个满足就会发送消息
        kafkaProps.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        kafkaProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
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
    /***
     * 异步发送消息，等同于send(record,null)，最简单的方式发送，不管消息是否正常到达
     **/
    public static void simpleSend(ProducerRecord record){
        try {
            producer.send(record);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 同步发送：一条消息发送后会阻塞当前线程，直到返回ack。
     */
    public static void sync(ProducerRecord record){
        try {
            RecordMetadata recordMetadata = (RecordMetadata) producer.send(record).get();
            logger.info("topic:" + recordMetadata.topic()+",partition:" + recordMetadata.partition()+",offset:" + recordMetadata.offset());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 异步发送
     */
    public static void aync(ProducerRecord record){
        producer.send(record, new DemonProducerCallback());
        //producer.flush();
    }

    /**
     * 回调函数：回调函数会在producer收到ack时调用，为异步调用。
     * 如果消息发送失败，则会自动重试，无需再回调函数中手动处理。
     **/
    private static class DemonProducerCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            //如果exception不为null，说明消息发送失败
            if (null != e){
                logger.error("消息发送失败",e.getMessage());
            }else{
                logger.info("消息发送成功[topic:" + recordMetadata.topic()+",partition:" + recordMetadata.partition()+",offset:" + recordMetadata.offset()+"]");
            }

        }
    }
}
