package com.zt.service;

import com.alibaba.fastjson.JSON;
import com.zt.Server;
import com.zt.model.StockServer;
import com.zt.service.ThreadInit;
import com.zt.util.PropertiesUtil;
import com.zt.util.ServerConfig;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.cisc.zztclient.ZZTClient;
import com.zt.task.DispatchTask;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ClientInit {

    private ClientInit() {

    }

    private static ZZTClient client = null;

    // 服务器信息
    private static String serverMsg = null;

    public static void init(String type) {
        String serverType = StringUtil.isNullOrEmpty(type) ? String.format("%s %s", ServerConfig.quotes, ServerConfig.TEST) : type;
        String[] param = serverType.split(" ");
        //1 初始化环境信息 默认 test环境,加载test配置文件
        ServerConfig.intPath(param.length < 2 ? ServerConfig.TEST : param[1]);
        //2 初始化 启动那个服务器 默认启动行情 quotes
        switch (param[0]) {
            case ServerConfig.trade:
                init(ServerConfig.stock_trade_url, ServerConfig.stock_trade_port);
                break;
            case ServerConfig.information:
                init(ServerConfig.stock_information_url, ServerConfig.stock_information_port);
                break;
            default:
                initPushJob2(); //加载行情定时器
//                init(ServerConfig.stock_quotes_url, ServerConfig.stock_quotes_port);
                init(); //行情服务
                break;
        }
    }

    // 初始化消息推送JOB
    public static void initPushJob() {
        try {
            JobDetail job = JobBuilder.newJob(DispatchTask.class).build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(PropertiesUtil.getConfig(ServerConfig.server_push))).build();
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
            // 初始化推送队列
            ThreadInit.initQueue();
        } catch (Exception e) {
            log.error("PUSH JOB INIT FAILD", e);
        }
    }

    // 初始化消息推送JOB、读取两个任务时间(能精确到分钟级别),只能分拆成两个才能完成需求
    public static void initPushJob2() {
        try {
            //因为一个cron表达式表达不出,用两个表达式
            JobDetail job = JobBuilder.newJob(DispatchTask.class).build();
            JobDetail job2 = JobBuilder.newJob(DispatchTask.class).build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(PropertiesUtil.getConfig(ServerConfig.server_push))).build();

            CronTrigger trigger2 = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(PropertiesUtil.getConfig(ServerConfig.server_push2))).build();

            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.scheduleJob(job, trigger);
            scheduler.scheduleJob(job2, trigger2);

            scheduler.start();
            // 初始化推送队列
            ThreadInit.initQueue();
        } catch (Exception e) {
            log.error("PUSH JOB INIT FAILD", e);
        }
    }

    /**
     * 电信,联通,移动站点
     */
    public static void init() {
        String yd = PropertiesUtil.getConfig(ServerConfig.SipChinaMobile);
        String lt = PropertiesUtil.getConfig(ServerConfig.SipChinaUnicom);
        String dx = PropertiesUtil.getConfig(ServerConfig.SipChinaTelecom);
        String totay = yd + lt + dx;
        String[] ipArry = totay.split(";");
        client = new ZZTClient();
        //配置多个ip、port端口 连接、随机请求查询
        for (int i = 0; i < ipArry.length; i++) {
            String[] ipPort = ipArry[i].split(":");
            if (ipPort.length < 2 || !ipPort[3].equals("HQ")) continue;
            client.registerServer("zt", ipPort[0], Integer.parseInt(ipPort[1]));
            System.err.println(String.format("创建 %s 个连接", i));
        }
    }


    public static void init(String ipkey, String portkey) {
        String ips = PropertiesUtil.getConfig(ipkey);
        String ports = PropertiesUtil.getConfig(portkey);
        String[] ipArry = ips.split(",");
        String[] portArry = ports.split(",");
        client = new ZZTClient();
        //配置多个ip、port端口 连接、随机请求查询
        for (int i = 0; i < ipArry.length; i++) {
            client.registerServer("zt", ipArry[i], Integer.parseInt(portArry[i]));
        }

    }

    public static ZZTClient getClient() {
//        if (client == null) {
//            init(serverType);
//        }
        return client;
    }


    // 获取服务器信息
    public static String getServerMsg() {
        if (StringUtil.isNullOrEmpty(serverMsg)) {
            initServerMsg();
        }
        return serverMsg;
    }

    // 初始化服务器信息
    public static void initServerMsg() {
        Map<String, Object> resultMap = new HashMap<>();
//		Properties properties = PropertiesUtil.readAll(PropertiesUtil.INPATH_SERVER);
        Properties properties = PropertiesUtil.outInit(ServerConfig.getZZTServerPath()); //TODO 现在没用
        properties.forEach((k, v) -> {
            String[] serverStr = v.toString().split(";");
            List<StockServer> servers = new ArrayList<>();
            for (String serverMsg : serverStr) {
                String[] msgArr = serverMsg.split(",");
                StockServer server = new StockServer();
                server.setDomain(msgArr[0]);
                server.setName(msgArr[1]);
                if (msgArr.length > 2) {
                    server.setType(msgArr[2]);
                }
                servers.add(server);
                resultMap.put(k.toString(), servers);
            }
        });
        resultMap.put("area", "message");
        serverMsg = JSON.toJSONString(resultMap);
    }

}
