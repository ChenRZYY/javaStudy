package com.sdrfengmi.springboot._002_resource;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * 四、利用PropertiesLoaderUtil加载配置文件
 */
public class RedisConfig3 {
 
    public static String  host;
    public static int  port;
    private static String property="redis-config.properties";
 
    private static RedisConfig3 mConfig;
    static {
        mConfig=loadConfig();
    }
    public  static  RedisConfig3 loadConfig(){
        if (mConfig==null){
            mConfig=new RedisConfig3();
            Properties properties = null;
            try {
                properties = PropertiesLoaderUtils.loadAllProperties(property);
 
                host=properties.getProperty("spring.redis.host");
                port=Integer.valueOf(properties.getProperty("spring.redis.port"));
                System.out.println(host+":"+port);
            } catch (IOException e) {
                e.printStackTrace();
            }
 
        }
        return mConfig;
    }
 
    public RedisConfig3 getInstance(){
        return mConfig;
    }
}
