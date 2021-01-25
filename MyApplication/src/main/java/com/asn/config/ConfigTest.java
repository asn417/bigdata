package com.asn.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private static Logger logger = LoggerFactory.getLogger(ConfigTest.class);

    private int age;
    private String name;

    //@value注解的作用就是提供get和set方法，如果显示提供了get和set方法，则可以不用value注解
    //如果@value和@ConfigurationProperties混合使用，则@ConfigurationProperties的优先级更高
    @Value("${config.test1.salary:500}")
    private long salary;
    //@Bean作用在方法上，将方法的返回对象作为bean对象注册到IOC容器中。默认的bean名称就是方法名称，可以通过name指定bean名称。
    @Bean(name = "dog")
    public Dog dog1(){
        logger.info("初始化Dog bean对象");
        return new Dog("doog",1);
    }

    @ConfigurationProperties(prefix = "config.dog")
    @Bean(name = "dog2")
    public Dog dog2(){
        return new Dog();
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
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
                ", salary=" + salary +
                '}';
    }
}
