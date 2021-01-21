package com.asn.aop;

import com.asn.producer.ProducerUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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
        System.out.println("==@Around== --》method name: " + methodName + " args: " + params[0]);

        String topic = annotation.topic();
        System.out.println("==@Around== --》topic " + topic);

        KafkaProducer<String, String> producer = ProducerUtil.getInstance();

        /*ProducerRecord<String, String> record = new ProducerRecord<>(topic,null,System.currentTimeMillis(),"message_key","message_value");
        try {
            producer.send(record);
        } catch(Exception e){
            e.printStackTrace();
        }*/

        if (topic.equals("test")){
            // 执行被切入的方法，这里可以修改方法参数params，重新传入即可：proceed(params)
            joinPoint.proceed();
        }else {
            params[0] = "topic1";
            joinPoint.proceed(params);
        }
    }

    /**
     * 获取方法中声明的注解
     *
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
}
