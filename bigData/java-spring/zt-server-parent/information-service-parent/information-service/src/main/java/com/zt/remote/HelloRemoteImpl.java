package com.zt.remote;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRemoteImpl implements HelloRemote {
    
    public static ConcurrentHashMap<Integer, String> countMap = new ConcurrentHashMap<Integer, String>();
    
    public int count = 1;
    
    @Override
    @RequestMapping(value = "/hello")
    public String hello(@RequestParam(value = "name") String name) {
        
        if (count<1000) {
            countMap.put(count, name);
            count++;
        }
        if (countMap.size() > 1000) {
            int nextInt = new Random().nextInt(1000);
//            countMap.remove(nextInt);
            countMap.putIfAbsent(nextInt, name);
        }
        System.err.println(count+"---"+countMap.size());
        return ""+count;
    }
    
}
