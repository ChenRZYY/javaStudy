package com.sdrfengmi.study._003_encrypt.autoRSAEncrypt;

import java.net.URL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEncrypt
@SpringBootApplication
public class App {
    
    //   1，后台方法上如果有@Encrypt注解和@RequestBody  RequestBodyAdvice 修饰的方法，需要进行参数的解密    
    //   2，后台方法上如果有@Encrypt注解和@ResponseBody ResponseBodyAdvice 修饰的方法，需要进行参数的加密
    // 根据请求前,请求后,对方法进行判断是否有注解,进行加密解密运算
    public static void main(String[] args) {
        URL urlOfClass = App.class.getClassLoader().getResource("org/slf4j/spi/LocationAwareLogger.class");
        System.out.println(urlOfClass);
        SpringApplication.run(App.class, args);
    }
    
}
