package com.zt.task;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.zt.service.ThreadInit;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zt.model.StockSubscriber;
import com.zt.service.StockSend;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisallowConcurrentExecution //加注解只能单线程
public class DispatchTask implements Job {

    private BlockingQueue<StockSubscriber> subscribers;

    private Set<Map.Entry<String, StockSubscriber>> entries;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            //每个都延迟500ms推送
//			Thread.sleep(500);
//			log.error("放入队列时间: "+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));

//            StockSubscriber subscriber = null;
            //	log.error("请求块数"+StockSend.getPushMap().size());
            //	log.error("开始放入队列: "+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
            // 放入消息队列 把所有的channelKey分给8个队列
            entries = StockSend.getPushMap().entrySet();
            for (Map.Entry<String, StockSubscriber> entry : entries) {
                subscribers = ThreadInit.getSubQueue();
                subscribers.put(entry.getValue()); // 有可能这边正在遍历,前端已经取消订阅,可以判断下再放入队列
            }
            //		log.error("放入队列"+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date())+"队列长度"+subscribers.size()+" 队列名称:"+subscribers.toString()+"请求块用户数:"+subscriber.getChannels().size());

        } catch (Exception e) {
            log.error("dispatch error", e);
        }
    }

}
