package com.zt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zt.information.feign.HelloFeign;

@RefreshScope
@RestController
public class TestController {

    @Value("${from}")
    private String from = "trade";
    
    @Autowired
    private HelloFeign helloFeign;

    @GetMapping("/zipkin")
    public String zipkin(@RequestParam("count") Integer count) {
    	System.err.println(from+"---"+count);
    	
    	String from2 = helloFeign.from();
        return from2;
    }
    
    
    @GetMapping("/from")
    public String from(@RequestParam("count") Integer count) {
    	System.err.println(from+"---"+count);
    	
//    	String from2 = helloFeign.from();
    	return from;
    }

}