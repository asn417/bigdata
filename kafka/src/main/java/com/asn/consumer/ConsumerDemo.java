package com.asn.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.*;

/**
 * @Author: wangsen
 * @Date: 2021/1/18 16:39
 * @Description: 消费者
 *  1、自动提交offset：需要配置两个参数（enable.auto.commit，auto.commit.interval.ms）
 *  2、手动提交offset：手动提交需要关闭自动提交enable.auto.commit=false。手动提交有两种，异步和同步，这两种都只能提交到kafka。
 *  3、自定义提交offset存储到外部存储系统。将消费消息和提交offset放到一个事务中，实现exactly once。
 *
 *  因为消费者有分区分配策略，那么在消费者组订阅的topic发生变化，或消费者组成员发生变化时，都会触发rebalance重分配分区操作。如果要自己维护offset，
 *  就需要自定义ConsumerRebalanceListener。
 **/
public class ConsumerDemo {
    private static Properties kafkaProps = new Properties();
    static {
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "flink1:9092,flink2:9092,flink3:9092");
        kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG,"group_id");
        kafkaProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");//关闭自动提交,使用手动提交
        //kafkaProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");//提交间隔
        kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
    }
    //存储最新的offset的集合
    public static Map<TopicPartition,Long> currentOffset = new HashMap<>();

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);
        /*consumer.subscribe(Arrays.asList("topic1"));
        while (true){
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord record:consumerRecords) {
                System.out.println("topic:"+record.topic()+",offset:"+record.offset()+",value:"+record.value());
            }
            //异步提交到kafka，还可以指定回调函数，或分区offset
            consumer.commitAsync();
            //同步提交到kafka
            consumer.commitSync();
        }*/
        consumer.subscribe(Arrays.asList("first"), new ConsumerRebalanceListener() {
            //rebalance之前调用，参数是再平衡之前，此消费者所分配的分区
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                //发生rebalance前提交分区偏移量。因为消费的时候是实时提交offset的，因此这里可以不用提交。
            }
            //rebalance之后调用，参数是再平衡之后，此消费者所分配的分区
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                for (TopicPartition partition:partitions){
                    Long offset = getPartitionOffset(partition);
                    //定位新分配的分区的offset，定位之后再poll消息就知道从哪个offset开始了
                    consumer.seek(partition,offset);
                }
            }
        });
        try {
            while (true){
                //拉取数据，参数是拉取数据的间隔
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                TopicPartition topicPartition = null;
                //为了保证数据的可靠性借助带有事务功能的数据库去存储offset
                //开启事务的代码
                for (ConsumerRecord<String, String> record : records) {
                    //下面消费和提交offset的代码用事务包起来，即可解决重复消费，实现exactly once。
                    System.out.println(record.topic() +"\t"+record.partition() +"\t"+ record.value() +"\t"+ record.offset());
                    topicPartition = new TopicPartition(record.topic(), record.partition());
                    //提交offset
                    commitOffset(topicPartition,record.offset()+1);
                }
            }
        } finally {
            consumer.close();
        }
    }

    private static void commitOffset(TopicPartition topicPartition, long offset) {
        //保存offset的具体代码，需要与getPartitionOffset中存储的位置一致
    }

    public static <T,K> Long getPartitionOffset(TopicPartition topicPartition){
        //根据topicPartition获取最新的offset的方法，这里需要看offset是维护在哪里的，比如mysql、hbase或Redis，去对应的地方获取即可
        return  0l;
    }
}
