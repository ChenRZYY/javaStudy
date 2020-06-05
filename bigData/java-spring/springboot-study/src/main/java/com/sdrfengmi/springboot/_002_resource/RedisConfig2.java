package com.sdrfengmi.springboot._002_resource;

/**
 * 三、@ConfigurationProperties找到该资源的头部，通过getter、setter方法注入及获取配置
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource({"classpath:redis-config.properties"})
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig2 {

    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}