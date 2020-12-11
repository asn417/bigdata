package com.asn.serializer;

import com.asn.utils.BeanUtils;
import org.apache.kafka.common.serialization.Serializer;
import java.util.Map;

public class MessageSerializer implements Serializer<Object> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String topic, Object data) {
        return BeanUtils.bean2Byte(data);
    }

    @Override
    public void close() {

    }
}
