package com.asn.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通过重写WebMvcConfigurer接口中的相关方法，实现个性化MVC配置
 */
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //注册一个名为sen的controller（相当于是requestmapping(value="sen")），并设置其对应的视图为test
        registry.addViewController("/sen").setViewName("test");
    }

}
