package com.sdrfengmi.springboot._002_resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * 二、@PropertySource获取resource下面的资源，@ConfigurationProperties找到该资源的头部@Value注入属性.
 */
@PropertySource({"classpath:redis-config.properties"})
@EnableConfigurationProperties({RedisConfig.class})
@ConfigurationProperties(prefix="spring.redis")
public class RedisConfig {
 
    @Value("${host}")
    private  String  host;
    @Value("${port}")
    private  int  port;
}