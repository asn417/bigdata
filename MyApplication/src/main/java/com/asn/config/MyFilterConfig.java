package com.asn.config;

import com.asn.filter.MyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;

@Configuration
public class MyFilterConfig {
    @Resource
    private MyFilter myFilter;
    @Bean
    public FilterRegistrationBean<Filter> registerFilter(){
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(myFilter);
        registration.addUrlPatterns("/*");
        registration.setName("myfilter");
        //过滤器排序，值越小越先执行
        registration.setOrder(1);
        return registration;
    }
}
