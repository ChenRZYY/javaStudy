package com.zt.controller;

import java.net.URLDecoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zt.feign.HelloRemoteFeign;
import com.zt.util.DESUtil;
import com.zt.util.DESUtils;
import com.zt.util.DesCipherUtil;

@RefreshScope
@RestController
public class TestController {

	@Autowired
	private HelloRemoteFeign helloRemoteFeign;
	
	
    @Value("${from}")
    private String from ;
    
    @Value("${foo}")
    private String foo = "foo";


    @GetMapping("/from")
    public String from() {
    	System.err.println(this.from+foo);
        return this.from+foo;
    }
    
    @GetMapping("/zipkin")
    public String zipkin(@RequestParam Integer count) {
    	
    	System.err.println(count);
    	String hello = helloRemoteFeign.from(count);
    	return "zipkin--"+hello+"-------------"+count;
    }
    
    @GetMapping("/des")
    public String des(@RequestParam(name = "des") String des) {
        String decryption = null;
        try {
            decryption = DESUtil.decryption(des, "12345678");
            
            String str="9519ed0705bef0f16dd3a1b572c6fed5";
            String decryption2 = DESUtils.decryption(str, "12345678");
            
            
            String encryption = DESUtil.encryption(des, "12345678");
            String str2HexStr = DESUtil.str2HexStr(encryption);
            
            
            String decrypt = DesCipherUtil.decrypt(des, "12345678");
            
            String encrypt = DesCipherUtil.encrypt("chenzhendong", "12345678");
            System.err.println(str2HexStr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return decryption;
    }
    
    
    
    @GetMapping("/des2")
    public String zipkin(@RequestParam(name = "des") String des) {
        String decryption = null;
        try {
            String str ="lcWE0UbKmLWG5xe1ILCFyjPcsWYtxqN6%2FrlZt9RkL8s%3D";
            
            String dec = URLDecoder.decode(str, "utf-8");
            
            String decrypt = DesCipherUtil.decrypt(dec, "12345678");
            String encrypt = DesCipherUtil.encrypt("chenzhendong陈振东{}?+++", "12345678");
            System.err.println(encrypt);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return decryption;
    }

}