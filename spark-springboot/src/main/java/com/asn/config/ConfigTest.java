package com.asn.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
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
    private int age;

    //@Bean作用在方法上，将方法的返回对象作为bean对象注册到IOC容器中。默认的bean名称就是方法名称，可以通过
    //name指定bean名称。
    @Bean(name = "dog")
    public Dog dog1(){
        return new Dog("doog",1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "ConfigTest{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
