package com.zt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableAutoConfiguration
@EnableFeignClients  //@EnableFeignClients(basePackages = {"com.renzku.eurekaClientTwo.feign"})  // 开启feign
@EnableHystrix // 打开hystrix断路器功能
@EnableHystrixDashboard //声明断路点HystrixCommand
//@EnableCircuitBreaker	//开启HystrixDashboard
public class InformationApplication {

	public static void main(String[] args) {
		SpringApplication.run(InformationApplication.class, args);
	}

	
}
