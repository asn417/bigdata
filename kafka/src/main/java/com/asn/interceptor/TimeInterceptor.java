package com.asn.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;
/**
 * 拦截器：增加时间戳
 */
public class TimeInterceptor implements ProducerInterceptor<String,String> {

    //这个方法是producer发送消息前被拦截
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        return new ProducerRecord<String, String>(record.topic(),
                record.partition(),
                record.timestamp(),
                record.key(),
                System.currentTimeMillis()+record.value(),
                record.headers());
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
