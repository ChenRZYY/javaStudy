package com.zt.task;

import java.util.concurrent.BlockingQueue;

import com.zt.model.StockSubscriber;
import com.zt.service.StockSend;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DispatchTask extends Thread {
    private StockSubscriber subscriber;

    private BlockingQueue<StockSubscriber> blockingQueue;

    public DispatchTask(BlockingQueue<StockSubscriber> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                log.error(this.toString()); //这个线程一直在线程池,不会销毁
                // 从消息队列取出 消息推送 for循环是否要优化
                subscriber = blockingQueue.take();
                //				System.err.println("队列长度"+blockingQueue.size());
                //		log.error("取出队列发送请求: " + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date())+" 请求参数: "+JSON.toJSONString(subscriber));
                //		Thread thread = Thread.currentThread();
                //		log.error("线程名称: "+thread.getName()+" 队列名称: "+ blockingQueue);
                //TimerSend.sendData(subscriber);
                if (subscriber.getChannels().size() > 0) {
                    StockSend.sendData(subscriber);
                } else { //TODO 这里移除有并发操作 ??? 什么时候移除空 channels的key对象
                    StockSend.cancel(subscriber.getChannelKey(), null);
                }


            } catch (Exception e) {
                log.error("DispatchTask run errror", e);
            }
        }
    }

}
