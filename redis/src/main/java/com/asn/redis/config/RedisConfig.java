package com.asn.redis.config;

import com.asn.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: wangsen
 * @Date: 2021/1/7 11:01
 * @Description:
 **/
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxTotal;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;//最小空闲连接数
    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWait;


    @Bean
    //@ConfigurationProperties("spring.redis.jedis.pool")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        jedisPoolConfig.setMaxTotal(maxTotal);

        return jedisPoolConfig;
    }

    @Bean(destroyMethod = "close")
    public JedisPool jedisPool() {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig(), host, port, timeout * 1000,password);
        RedisUtil.initJedis(jedisPool);
        return jedisPool;
    }

}
