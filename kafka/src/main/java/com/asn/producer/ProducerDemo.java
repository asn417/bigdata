package com.asn.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

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
 *  send是异步的，会返回一个Future用于阻塞RecordMetadata的获取，通过调用send(record).get()方法实现阻塞。
 *
 *  要想完全非阻塞，则可以使用Callback参数来提供一个回调函数，当请求完成时将调用该回调。同一个分区中的消息如何使用了多个回调函数，那么这些回调函数会按顺序依次执行。
 *  当在事务中使用时，不必定义callback函数或通过future获取结果来检查send是否失败。因为一旦有消息发送失败，就会调用final中的commitTransaction函数来抛出异常。这时候需要你调用abortTransaction来重置消息状态并继续发送消息。
 *
 **/
public class ProducerDemo {
    private static Properties kafkaProps = new Properties();
    static {
        kafkaProps.put("bootstrap.servers", "flink1:9092,flink2:9092,flink3:9092");
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    }

    public static void main(String[] args) {
        KafkaProducer<String, String> producer = new KafkaProducer(kafkaProps);
        ProducerRecord<String, String> record = new ProducerRecord<>("test","message_key","message_value");

        ProducerRecord<String, String> recordWithCreateTime = new ProducerRecord<>("test",null,System.currentTimeMillis(),"message_key","message_value");

        simpleSend(producer, record);

        sync(producer, record);

        aync(producer, record);
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
     * 同步发送
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
            while (true){
                Thread.sleep(10 * 1000);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /***
     * @Author: wangsen
     * @Description: 回调函数
     * @Date: 2020/12/5
     * @Param:
     * @Return:
     **/
    private static class DemonProducerCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
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
