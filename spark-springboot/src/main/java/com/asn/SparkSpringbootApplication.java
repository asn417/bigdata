package com.asn;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.asn.sparkspringboot.mapper")
@EnableApolloConfig
public class SparkSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SparkSpringbootApplication.class, args);
    }

}
