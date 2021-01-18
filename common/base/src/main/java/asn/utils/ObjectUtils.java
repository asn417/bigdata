package asn.utils;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Author: wangsen
 * @Date: 2021/1/17 19:02
 * @Description:
 **/
public class ObjectUtils {
    private static final Logger logger = LoggerFactory.getLogger(ObjectUtils.class);

    /**
     * 反射获取对象的属性值
     * @author Justin Teng
     * @date 2019/6/22
     * @param sourceData
     * @param attrName
     * @return
     */
    public static Object getValue(Object sourceData, String attrName) throws ReflectiveOperationException {
        if (sourceData == null) {
            return null;
        }
        if (sourceData instanceof Map) {
            return ((Map) sourceData).get(attrName);
        } else if (sourceData instanceof Integer || sourceData instanceof Double || sourceData instanceof Float
                || sourceData instanceof String || sourceData instanceof Date || sourceData instanceof BigDecimal
                || sourceData instanceof Long || sourceData instanceof Timestamp) {
            // 基础数据类型直接返回
            return sourceData;
        }
        if (StrUtil.isEmpty(attrName)){
            return null;
        }
        Field field = sourceData.getClass().getDeclaredField(attrName);
        field.setAccessible(true);
        Object result = field.get(sourceData);
        return result;
    }



    /**
     * @Description 不全为空
     * @author Justin Teng
     * @date 2019/9/29
     * @param obj
     * @return boolean
     */
    public static boolean allNotNull(Object... obj){
        boolean result = true;
        if (obj == null){
            result = false;
        } else {
            for (Object t : obj) {
                if (t == null) {
                    result = false;
                }
            }
        }
        return result;
    }


    /**
     * 深拷贝
     * @author Justin Teng
     * @date 20190502
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> T deepClone(T obj){
        T clonedObj = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            out.close();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            clonedObj = (T) in.readObject();
            in.close();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return clonedObj;
    }

    /**
     * 对象转数组
     * @param obj
     * @return
     */
    public static  byte[] toByteArray (Object obj) {
        if(obj==null){
            return null;
        }
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            logger.error("Object is null",ex);
        }
        return bytes;
    }

    /**
     * 数组转对象
     * @param bytes
     * @return
     */
    public static Object toObject (byte[] bytes) {
        if(bytes==null){
            return null;
        }
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (Exception ex) {
            logger.error("Object is null",ex);
        }
        return obj;
    }

    /**
     * 非空执行消费方法
     * @author Justin Teng
     * @date 2020/5/13
     * @param t
     * @param consumer
     * @return void
     */
    public static <T> void nonNullThenConsume(T t, Consumer<T> consumer) {
        if (t != null) {
            consumer.accept(t);
        }
    }
}
