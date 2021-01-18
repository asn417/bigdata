package com.asn.redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

/**
 * @Author: wangsen
 * @Date: 2021/1/7 11:47
 * @Description:
 **/
public class RedisUtil {

    private static Jedis jedis;
    public static void initJedis(JedisPool jedisPool){
        if(jedis == null){
            synchronized (Jedis.class){
                jedis = jedisPool.getResource();
            }
        }
    }

    /**
     * 设置超时时间，秒
     **/
    public static void set(String key,String value,int secondsToExpire){
        SetParams setParams = new SetParams();
        setParams.ex(secondsToExpire);
        jedis.set("key","value",setParams);
    }

    public static String get(String key){
        return jedis.get(key);
    }

    public static void set(String key,String value){
        jedis.set(key,value);
    }

    public static Long del(String key){
        return jedis.del(key);
    }
}
