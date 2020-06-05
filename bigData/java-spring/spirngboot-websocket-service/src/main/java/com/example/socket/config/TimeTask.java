package com.example.socket.config;

import com.example.socket.code.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author 陈振东
 * @create 2020/6/4 09:49
 */
@EnableScheduling   // 2.开启定时任务
@Slf4j
@Component
public class TimeTask {

    @Autowired
    private WebSocket webSocket;

    //3.添加定时任务
    //    @Scheduled(cron = "0/3 * * * * ?")
    //或直接指定时间间隔，例如：3秒
    @Scheduled(fixedRate = 3000)
    public void configureTasks() {
        webSocket.GroupSending("服务器3s心跳");
        log.info("-------------------定时器3s发送心跳--------------------------");
    }

}
