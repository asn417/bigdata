package asn.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 */
@Component
@Lazy(false)
public class SpringContextUtil implements ApplicationContextAware,BeanFactoryAware {
    private static final Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);
    private static ApplicationContext applicationContext;
    private static BeanFactory beanFactory;
    private static String appName;
    private static String contextName;

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }
    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static String getAppName() {
        if(appName==null){
            String applicationName = getBean(Environment.class).getProperty("spring.application.name");
            //取wy
            appName = applicationName.substring(11,applicationName.length());
        }
        return appName;
    }

    //获取上下文名称，用户缓存和获取上下文
    public static String getContextName() {
        if(contextName==null){
            //先获取contextName， 如果没有取appName
            contextName = getBean(Environment.class).getProperty("spring.application.contextName");
            if(contextName==null) {
                contextName = getAppName() ;
            }
        }
        return contextName;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static boolean hasBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取某个类的名字
     */
    public static String [] getBeanNames(Class clazz) {
        checkApplicationContext();
        return applicationContext.getBeanNamesForType(clazz);
    }
    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> List<T> getBeans(Class<T> clazz) {
        checkApplicationContext();
        String [] beanNames =  applicationContext.getBeanNamesForType(clazz);
        List<T> list = new ArrayList<>();
        if(beanNames!=null && beanNames.length > 0){
            for(String beanName:beanNames){
                list.add(applicationContext.getBean(beanName,clazz));
            }
        }
        return list;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 清除applicationContext静态变量.
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext未注入,请在spring-mvc.xml中定义SpringContextHolder");
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
