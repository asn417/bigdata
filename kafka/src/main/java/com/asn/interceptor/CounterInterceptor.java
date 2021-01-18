package com.asn.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * 拦截器：统计发送成功和失败的消息数量
 */
public class CounterInterceptor implements ProducerInterceptor<String,String> {
    private long successNum = 0l;
    private long failedNum = 0l;

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        return record;
    }

    //这个方法是producer接收ack前被拦截
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null){
            successNum++;
        }else {
            failedNum++;
        }
    }

    //close会在producer调用close方法时调用
    @Override
    public void close() {
        System.out.println("successNum="+successNum);
        System.out.println("failedNum="+failedNum);
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
