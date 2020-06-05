package com.example.socket.config;

import com.example.socket.code.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private WebSocketService webSocketService;

    //3.添加定时任务
    //    @Scheduled(cron = "0/3 * * * * ?")
    //或直接指定时间间隔，例如：3秒
    @Scheduled(fixedRate = 100000)
    public void configureTasks() {
        webSocketService.groupSending("{\n" +
                "\t\"params\": {\n" +
                "\t\t\"oneStock_min\": {\n" +
                "\t\t\t\"BuySellData\": 1,\n" +
                "\t\t\t\"Action\": 20109,\n" +
                "\t\t\t\"T64\": 1,\n" +
                "\t\t\t\"Adjoin\": 1,\n" +
                "\t\t\t\"GRID3\": 1,\n" +
                "\t\t\t\"StartPos\": 0,\n" +
                "\t\t\t\"AccountIndex\": 2,\n" +
                "\t\t\t\"Level\": 2,\n" +
                "\t\t\t\"StockCode\": \"000651\",\n" +
                "\t\t\t\"channelKey\": \"oneStock_min_fenshi_000651\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n");
        log.info("-------------------定时器3s发送心跳--------------------------");
    }

}
