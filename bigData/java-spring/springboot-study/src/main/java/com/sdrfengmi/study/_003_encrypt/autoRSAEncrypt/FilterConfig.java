package com.sdrfengmi.study._003_encrypt.autoRSAEncrypt;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

//@Configuration
public class FilterConfig {

    @Bean
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public FilterRegistrationBean filterRegistration() {
    	EncryptionConfig config = new EncryptionConfig();
    	config.setKey("abcdef0123456789");
    	config.setRequestDecyptUriList(Arrays.asList("/save", "/decryptEntityXml"));
    	config.setResponseEncryptUriList(Arrays.asList("/encryptStr", "/encryptEntity", "/save", "/encryptEntityXml", "/decryptEntityXml"));
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new EncryptionFilter(config));
        //registration.setFilter(new EncryptionFilter(config, new RsaEncryptAlgorithm()));
        registration.addUrlPatterns("/*");
        registration.setName("EncryptionFilter");
        registration.setOrder(1);
        return registration;
    }

}