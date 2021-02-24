package com.asn.aop;

import cn.hutool.json.JSONUtil;
import com.asn.application.topN.UserAction;
import com.asn.producer.ProducerUtil;
import com.asn.utils.LogUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wangsen
 * @Date: 2020/12/5 16:18
 * @Description: 定义日志切面
 * Lazy注解: 容器启动时会初始化所有bean，如果想让某个bean在使用时才初始化，以便缩短容器启动时间，可以在类上加上此注解，默认值为true。
 *
 * spring的5种通知类型：
 * 1)前置通知 (@Before)
 * 2)返回通知 (@AfterReturning)
 * 3)异常通知 (@AfterThrowing)
 * 4)后置通知 (@After)
 * 5)环绕通知 (@Around):（优先级最高）
 *
 * 同一个切面中的通知顺序：around -> before -> method -> afterReturning/afterThrowing -> after
 **/
@Aspect
@Component
@Lazy(false)
public class LogAspect {
    private static Logger mylogger = LoggerFactory.getLogger("mylogger");
    //用户的行为列表
    List<String> userBehaviors = Arrays.asList("pv", "buy", "cart", "fav");
    /**
     * 定义切入点：拦截所有使用了com.asn.aop.LogToKafka注解的方法
     */
    @Pointcut("@annotation(com.asn.aop.LogToKafka)")
    private void cutMethod() {}

    /**
     * 前置通知：在目标方法执行前调用
     */
    @Before("cutMethod()")
    public void begin() {
        System.out.println("==@Before==");
    }

    /**
     * 后置通知：在目标方法执行后调用，若目标方法出现异常，则不执行
     */
    @AfterReturning("cutMethod()")
    public void afterReturning() {
        System.out.println("==@AfterReturning==");
    }

    /**
     * 后置/最终通知：无论目标方法在执行过程中是否出现异常都会在它之后调用
     */
    @After("cutMethod()")
    public void after() {
        System.out.println("==@After==");
    }

    /**
     * 异常通知：目标方法抛出异常时执行
     */
    @AfterThrowing("cutMethod()")
    public void afterThrowing() {
        System.out.println("==@AfterThrowing==");
    }

    /**
     * 环绕通知：最先执行，需要显式调用目标方法，否则被拦截的方法不会执行。
     */
    @Around("cutMethod()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取目标方法的名称
        String methodName = joinPoint.getSignature().getName();
        // 获取方法传入参数
        Object[] params = joinPoint.getArgs();
        LogToKafka annotation = getDeclaredAnnotation(joinPoint);
        String topic = annotation.topic();
        if ( params[0] != null){
            topic =  (String) params[0];
        }

        ProducerUtil.getInstance();
        ProducerRecord<String, String> record;
        /*for (int i = 0; i < 100; i++) {
            Map<String,Object> message = createMessage();
            String messageJson = JSONUtil.toJsonStr(message);
            TimeUnit.MILLISECONDS.sleep(100);
            record = new ProducerRecord<>(topic,null,System.currentTimeMillis(),null,messageJson);

            UserAction userAction = new UserAction();
            userAction.setUserId(RandomUtils.nextLong(1, 100));
            userAction.setItemId(RandomUtils.nextLong(1, 1000));
            userAction.setCategoryId(RandomUtils.nextInt(1, 30));
            userAction.setBehavior(userBehaviors.get(RandomUtils.nextInt(0, 3)));
            userAction.setTimestamp(System.currentTimeMillis());
            //转换成JSON
            String userActionJson = JSONUtil.toJsonStr(userAction);
            record = new ProducerRecord<>(topic,null,System.currentTimeMillis(),null,userActionJson);

            ProducerUtil.aync(record);
        }*/
        Map<String,Object> message = createMessage();
        message.put("startTime",System.currentTimeMillis());

        joinPoint.proceed(params);

        message.put("endTime",System.currentTimeMillis());
        String messageJson = JSONUtil.toJsonStr(message);
        record = new ProducerRecord<>(topic,null,System.currentTimeMillis(),null,messageJson);
        //ProducerUtil.aync(record);

        mylogger.info(messageJson);
    }

    /**
     * 获取方法中声明的注解
     * @param joinPoint
     * @return
     * @throws NoSuchMethodException
     */
    public LogToKafka getDeclaredAnnotation(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();
        // 反射获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();
        // 拿到方法对应的参数类型
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        // 根据类、方法、参数类型（重载）获取到方法的具体信息
        Method objMethod = targetClass.getMethod(methodName, parameterTypes);
        // 拿到方法定义的注解信息
        LogToKafka annotation = objMethod.getDeclaredAnnotation(LogToKafka.class);
        // 返回
        return annotation;
    }

    private Map<String,Object> createMessage(){
        Map<String,Object> message = new HashMap<>();
        message.put("userID",LogUtil.getUserID());
        message.put("province", LogUtil.getProvince());
        message.put("productID",LogUtil.getProductID());
        message.put("productTypeID",LogUtil.getProductTypeID());
        message.put("price",LogUtil.getPrice());
        message.put("timeStamp",LogUtil.getTimeStamp());
        return message;
    }
}
