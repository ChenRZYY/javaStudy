package com.example.socket.config;

import com.example.socket.code.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

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

    public static AtomicInteger atoCount = new AtomicInteger();

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

    @Scheduled(fixedRate = 1000)
    public void send61003() {
        String message = "{\n" +
                "    \"params\": {\n" +
                "        \"jy_data\": {\n" +
                "            \"action\": \"61003\",\n" +
                "            \"reqNo\": \"213\",\n" +
                "            \"area\": \"902\",\n" +
                "            \"macAddr\": \"11-22-33-AA-BB-CC@192168001001@W\",\n" +
                "            \"token\": \"ed44ab19643e4edd90343e1a033cd5a4\",\n" +
                "            \"cuacct_code\": \"10998689\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        int count = 0;
        while (count < 3) {
            webSocketService.groupSending(message);
            atoCount.incrementAndGet();
            count++;
        }
        log.info("-------------------定时器3s发送心跳 send61306--------------------------");
    }

    @Scheduled(fixedRate = 1000)
    public void send61306() {
        String message = "{\n" +
                "    \"params\":{\n" +
                "        \"jy_data\":{\n" +
                "               \"action\": \"61306\",\n" +
                "        \"reqNo\": \"782\",\n" +
                "        \"macAddr\": \"11-22-33-AA-BB-CC@192168001001@W\",\n" +
                "        \"token\": \"cb17e531656a475aa4b255edcdee5961\",\n" +
                "        \"cuacct_code\": \"10997002\",\n" +
                "        \"cust_code\": \"18399656\",\n" +
                "        \"currency\": \"0\",\n" +
                "        \"fi_reserve\": \"8000\",\n" +
                "        \"collateral_reserve\": \"9000\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        int count = 0;
        while (count < 3) {
            webSocketService.groupSending(message);
            atoCount.incrementAndGet();
            count++;
        }
        log.info("-------------------定时器3s发送心跳 send61306 --------------------------");
    }

    @Scheduled(fixedRate = 1000)
    public void send69005() {
        try {
            String message = "{\n" +
                    "    \"params\":{\n" +
                    "        \"jy_data\":{\n" +
                    "            \"action\": \"69005\",\n" +
                    "        \"reqNo\": \"898\",\n" +
                    "        \"token\": \"cb17e531656a475aa4b255edcdee5961\",\n" +
                    "        \"macAddr\": \"11-22-33-AA-BB-CC@192168001001@W\",\n" +
                    "        \"cuacct_code\": \"10997002\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            int count = 0;
            while (count < 3) {
                webSocketService.groupSending(message);
                Thread.sleep(100);
                atoCount.incrementAndGet();
                count++;
            }
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("-------------------定时器3s发送心跳 send69005--------------------------");
    }

}
