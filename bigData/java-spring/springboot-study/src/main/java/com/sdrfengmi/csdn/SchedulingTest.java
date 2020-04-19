package com.sdrfengmi.csdn;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author:
 * @description:
 * @date: 2019-05-30 23:36
 */
@Component
public class SchedulingTest {
    private int i = 0;

    @Scheduled(fixedRate = 60 * 1000)
//具体时间间隔，60*1000也就是1分钟执行一次
    void doSomethingWith() {
        //371积分
//        String url = "https://blog.csdn.net/qq_34982426/article/details/82181344";//670/709
//        String url = "https://blog.csdn.net/sdrfengmi/article/details/87920468"; //239 /6116/6982 /269057/265911
        String url = "https://blog.csdn.net/sdrfengmi/article/details/90671850"; //239 /6116/6982/7076 /269057/265911
        Tool.doGet(url);
        i++;
        System.out.println("第" + i + "次访问");
    }
}

