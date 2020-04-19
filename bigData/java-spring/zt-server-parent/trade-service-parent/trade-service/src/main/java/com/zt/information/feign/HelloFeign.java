package com.zt.information.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${feign.information-service}",path="/${feign.information-service}")
public interface HelloFeign {

	@GetMapping("/from")
	String from();
	
	@GetMapping("/hello")
	String hello(@RequestParam("count") Integer count);
	
	@GetMapping("/world")
	String world(@RequestParam("count") Integer count);
}
