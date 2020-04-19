package com.zt.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "trade-service", fallback = HelloRemoteFeignHystrix.class)
//url = "http://localhost:1051/trade-service"
@FeignClient(value = "${feign.trade-service}",path="/${feign.trade-service}")
public interface HelloRemoteFeign {

	@GetMapping("/from")
	String from(@RequestParam("count") Integer count);
	
	@GetMapping("/hello")
	String hello(@RequestParam("count") Integer count);
	
	@GetMapping("/world")
	String world(@RequestParam("count") Integer count);
}
