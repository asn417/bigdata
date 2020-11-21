package com.asn.sparkspringboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.asn.sparkspringboot.mapper")
public class SparkSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SparkSpringbootApplication.class, args);
    }

}
