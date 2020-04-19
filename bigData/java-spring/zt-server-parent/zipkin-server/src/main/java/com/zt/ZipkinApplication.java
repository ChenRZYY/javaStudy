package com.zt;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.web.bind.annotation.RestController;

import zipkin.server.internal.EnableZipkinServer;


@SpringCloudApplication
//@EnableRedisHttpSession
@EnableZipkinServer 
@RestController
public class ZipkinApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinApplication.class, args);
    }
	
}
