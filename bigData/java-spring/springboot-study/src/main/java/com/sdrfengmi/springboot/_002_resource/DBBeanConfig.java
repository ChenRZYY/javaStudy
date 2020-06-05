package com.sdrfengmi.springboot._002_resource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 一、利用@PropertySource获取resource下面的资源，Environment获取属性
 */
@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:DBsource.properties"})
public class DBBeanConfig {
    @Autowired
    private Environment env;

    @Bean()
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(env.getProperty("source.driverClassName").trim());
        dataSource.setUrl(env.getProperty("source.url").trim());
        dataSource.setUsername(env.getProperty("source.username").trim());
        dataSource.setPassword(env.getProperty("source.password").trim());
        return dataSource;
    }
}