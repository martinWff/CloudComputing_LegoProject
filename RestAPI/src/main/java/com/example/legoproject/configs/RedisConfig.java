package com.example.legoproject.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Value("${redis.uri}")
    private String uri;

    @Value("${redis.port:6380}")
    private String port;

    @Value("${redis.key}")
    private String key;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        // Optional tuning — good for showing understanding
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);

        poolConfig.setBlockWhenExhausted(true);

        return new JedisPool(poolConfig, uri, 6380, 2000,key,true);
    }
}
