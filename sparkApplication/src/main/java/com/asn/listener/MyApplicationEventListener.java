package com.asn.listener;

import com.asn.config.Dog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Author: wangsen
 * @Date: 2021/1/19 14:29
 * @Description:
 * spring boot中支持的事件类型如下（按启动顺序）：
 *      1) SpringApplicationEvent：获取SpringApplication
 *      2) ApplicationEnvironmentPreparedEvent：环境事先准备
 *      3) ApplicationPreparedEvent：上下文context准备时触发
 *      4) ApplicationStartedEvent：spring boot 启动监听类
 *      5) ApplicationFailedEvent：该事件为spring boot启动失败时的操作
 *      6) ApplicationReadyEvent：上下文已经准备完毕的时候触发。这时候就可以获取容器中的所有bean信息。
 **/
@Component
public class MyApplicationEventListener implements ApplicationListener<ApplicationReadyEvent> {

    private static Logger logger = LoggerFactory.getLogger(MyApplicationEventListener.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();

        //获取beanfactory中定义的bean数量
        int beanDefinitionCount = applicationContext.getBeanDefinitionCount();
        logger.info("beanDefinitionCount ==> "+beanDefinitionCount);
        logger.info("=== 上下文准备完毕 ===");
    }
}
