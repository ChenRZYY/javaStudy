package com.zt.task;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.collect.Sets;
import com.zt.model.StockSubscriber;
import com.zt.util.ServerUtil;
import com.zt.util.StockUtil;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisallowConcurrentExecution //加注解只能单线程
public class DispatchTask implements Job {
    
    private BlockingQueue<StockSubscriber> queue;
    
    private Collection<StockSubscriber> subs;
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            
            //log.info("放入队列"+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date())+"队列长度"+StockUtil.getPushMap().size());
            //		log.error("放入队列"+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date())+"队列长度"+subscribers.size()+" 队列名称:"+subscribers.toString()+"请求块用户数:"+subscriber.getChannels().size());
            //      Collection<StockSubscriber> subs = TimerSend.getPushMap().values();
            
            HashSet<Channel> channelCount = Sets.newHashSet();//用户数量
            int stockSub = 0;//订阅数量
            
            subs = StockUtil.getPushMap().values();
            for (StockSubscriber sub : subs) {
                queue = ServerUtil.getSubQueue();// 放入消息队列 把所有的channelKey分给8个队列
                if (sub != null) { // 有可能这边正在遍历,前端已经取消订阅
                    queue.put(sub);
                    channelCount.addAll(sub.getChannels());
                    stockSub = sub.getChannels().size() > 0 ? stockSub + 1 : stockSub;
                }
            }
            log.error("用户数量: " + channelCount.size() + " 订阅数量:" + stockSub);
        }
        catch (Exception e) {
            log.error("dispatch error", e);
        }
    }
    
}
