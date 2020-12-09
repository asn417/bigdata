package com.asn.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wangsen
 * @Date: 2020/12/9 19:09
 * @Description:
 **/
@Configuration
@ConfigurationProperties(prefix = "config.test")
public class ConfigTest {
    //@Value("${config.test.name:xxx}")
    //value注解的作用就是提供get和set方法，如果显示提供了get和set方法，则可以不用value注解
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ConfigTest{" +
                "name='" + name + '\'' +
                '}';
    }
}
