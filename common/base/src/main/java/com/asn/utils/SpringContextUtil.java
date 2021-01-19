package com.asn.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实现ApplicationContextAware接口的类，能获取spring容器中的所有bean。
 * 实现BeanFactoryAware 接口也可以获取所有的bean。
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);
    private static ApplicationContext applicationContext;
    private static String appName;
    private static String contextName;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static String getAppName() {
        if(appName==null){
            Environment environment = getBean(Environment.class);
            String applicationName = environment.getProperty("spring.application.name");
            appName = applicationName;
        }
        return appName;
    }
    public static String getContextName() {
        if(contextName==null){
            //先获取contextName，如果没有取appName
            contextName = getBean(Environment.class).getProperty("spring.application.contextName");
            if(contextName==null) {
                contextName = getAppName() ;
            }
        }
        return contextName;
    }

    public static boolean hasBean(String name) {
        return applicationContext.containsBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取某个类的名字
     */
    public static String [] getBeanNames(Class clazz) {
        return applicationContext.getBeanNamesForType(clazz);
    }
    public static <T> List<T> getBeans(Class<T> clazz) {
        String [] beanNames =  applicationContext.getBeanNamesForType(clazz);
        List<T> list = new ArrayList<>();
        if(beanNames!=null && beanNames.length > 0){
            for(String beanName:beanNames){
                list.add(applicationContext.getBean(beanName,clazz));
            }
        }
        return list;
    }

    public static <T> Map<String,T> getBeansOfType(Class<T> clazz){
        return applicationContext.getBeansOfType(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }
}
