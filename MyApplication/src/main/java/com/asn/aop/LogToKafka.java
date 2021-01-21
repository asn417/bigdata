package com.asn.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: wangsen
 * @Date: 2020/12/5 17:01
 * @Description:
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogToKafka {
    String topic() default "test";//默认发送到名为test的topic
}
