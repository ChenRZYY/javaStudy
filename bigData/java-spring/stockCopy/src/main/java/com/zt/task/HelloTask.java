package com.zt.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.zt.model.StockSubscriber;
import com.zt.util.ServerUtil;
import com.zt.util.StockUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务类
 *
 * @author mjp
 */
@Slf4j
public class HelloTask {

    private BlockingQueue<StockSubscriber> subscribers;

    private Set<String> keys;

    public void hello() {
//        System.out.println("hello, Spring task!");
        execute();
    }

    public void execute() {
        try {
            //每个都延迟500ms推送
            //          Thread.sleep(500);
            int size = StockUtil.getPushMap().size();
            log.error("所有连接的长度: " + size + "放入队列时间: "
                    + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
//            int count = 1;
//            for (String key : StockUtil.getPushMap().keySet()) {
//                log.error(count++ + "存储的值" + key + StockUtil.getPushMap().get(key).getChannels().size());
//            }

            //          System.err.println("context  "+StockWebSocketServerHandler.contextMap.size());
            //          System.err.println("http  "+StockWebSocketServerHandler.SocketMap.size());

            StockSubscriber subscriber = null;
            keys = StockUtil.getPushMap().keySet();
            //  log.error("请求块数"+StockUtil.getPushMap().size());
            //  log.error("开始放入队列: "+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
            // 放入消息队列 把所有的channelKey分给8个队列
            for (String key : keys) {
                subscribers = ServerUtil.getSubQueue();
                subscriber = StockUtil.getPushMap().get(key);// 有可能这边正在遍历,前端已经取消订阅
                if (subscriber != null) {
                    subscribers.put(subscriber);
                }
                //      log.error("放入队列"+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date())+"队列长度"+subscribers.size()+" 队列名称:"+subscribers.toString()+"请求块用户数:"+subscriber.getChannels().size());
            }
        } catch (Exception e) {
            log.error("dispatch error", e);
        }
    }
}
