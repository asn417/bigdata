package com.asn.producer;

import com.asn.message.MessageVo;
import org.apache.kafka.clients.producer.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author: wangsen
 * @Date: 2020/12/5 13:43
 * @Description: kafka 生产者，参考：https://www.cnblogs.com/EnzoDin/p/12593406.html
 *  kafka生产者发送消息最终调用的都是send(ProducerRecord<K, V> record, Callback callback)方法。
 *  这个方法是异步的，每条消息会被发送到buffer缓存中（提高效率），发送到buffer后此方法会立即返回一个RecordMetadata对象。
 *  这个RecordMetadata对象包含了消息的partition信息、消息的offset信息以及时间戳（主要是解决分区重分配后过期日志的删除问题，此外还可以用于flink流处理时采用不同的时间机制）等。
 *  时间戳有两种类型，分别是CREATE_TIME(0)和LOG_APPEND_TIME(1)。前者表示消息的创建时间，由用户自己提供，如果没有提供则会自动使用消息的发送时间。后者是消息的写入时间，就是消息写入log时的broker本地时间。
 *
 *  如何使用时间戳？https://www.cnblogs.com/huxi2b/p/6050778.html
 *  Kafka broker config提供了一个参数：log.message.timestamp.type来统一指定集群中的所有topic使用哪种时间戳类型。用户也可以为单个topic设置不同的时间戳类型，具体做法是创建topic时覆盖掉全局配置：
 *  bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test --partitions 1 --replication-factor 1 --config message.timestamp.type=LogAppendTime
 *  另外， producer在创建ProducerRecord时可以指定时间戳:
 *  record = new ProducerRecord<String, String>("my-topic", null, System.currentTimeMillis(), "key", "value");
 *
 *  send是异步的，会返回一个Future用于阻塞RecordMetadata的获取，通过调用send(record).get()方法实现阻塞同步发送。
 *
 *  要想完全非阻塞，则可以使用Callback参数来提供一个回调函数，当请求完成时将调用该回调。同一个分区中的消息如何使用了多个回调函数，那么这些回调函数会按顺序依次执行。
 *  当在事务中使用时，不必定义callback函数或通过future获取结果来检查send是否失败。因为一旦有消息发送失败，就会调用final中的commitTransaction函数来抛出异常。这时候需要你调用abortTransaction来重置消息状态并继续发送消息。
 *
 **/
public class ProducerDemo {
    private static Properties kafkaProps = new Properties();
    static {
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "flink1:9092,flink2:9092,flink3:9092");
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "com.asn.serializer.MessageSerializer");
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

    public static void main(String[] args) {
        List<String> interceptors = new ArrayList<>();
        interceptors.add("com.asn.interceptor.TimeInterceptor");
        interceptors.add("com.asn.interceptor.CounterInterceptor");
        //添加拦截器
        kafkaProps.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,interceptors);

        KafkaProducer<String, String> producer = new KafkaProducer(kafkaProps);
        //ProducerRecord<String, String> record = new ProducerRecord<>("test","message_key","message_value");
        //ProducerRecord<String, String> recordWithCreateTime = new ProducerRecord<>("test",null,System.currentTimeMillis(),"message_key","message_value");
        for (int i = 0; i < 20; i++) {
            MessageVo messageVo = new MessageVo(i, String.valueOf(new Random().nextInt(10)), LocalDateTime.now());
            ProducerRecord<String, Object> record = new ProducerRecord<>("test",messageVo);
            simpleSend(producer, record);
            //sync(producer, record);
            //aync(producer, record);

        }
        producer.close();
    }
    /***
     * @Author: wangsen
     * @Description: 异步发送消息，等同于send(record,null)，最简单的方式发送，不管消息是否正常到达
     * @Date: 2020/12/5
     * @Param: [producer, record]
     * @Return: void
     **/
    public static void simpleSend(KafkaProducer producer, ProducerRecord record){
        try {
            producer.send(record);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 同步发送：一条消息发送后会阻塞当前线程，直到返回ack。
     *
     * @param producer
     * @param record
     */
    public static void sync(KafkaProducer producer, ProducerRecord record){
        try {
            RecordMetadata recordMetadata = (RecordMetadata) producer.send(record).get();
            System.out.println("topic:" + recordMetadata.topic());
            System.out.println("partition:" + recordMetadata.partition());
            System.out.println("offset:" + recordMetadata.offset());
            System.out.println("metaData:" + recordMetadata.toString());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 异步发送
     * @param producer
     * @param record
     */
    public static void aync(KafkaProducer producer, ProducerRecord record){
        try {
            producer.send(record, new DemonProducerCallback());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /***
     * @Author: wangsen
     * @Description: 回调函数：回调函数会在producer收到ack时调用，为异步调用。
     *  如果消息发送失败，则会自动重试，无需再回调函数中手动处理。
     **/
    private static class DemonProducerCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            //如果exception不为null，说明消息发送失败
            if (null != e){
                e.printStackTrace();
            }else{
                System.out.println("topic:" + recordMetadata.topic());
                System.out.println("partition:" + recordMetadata.partition());
                System.out.println("offset:" + recordMetadata.offset());
                System.out.println("metaData:" + recordMetadata.toString());
            }

        }
    }
}
