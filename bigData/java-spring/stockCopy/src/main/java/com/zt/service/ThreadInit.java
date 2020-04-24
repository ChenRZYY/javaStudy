package com.zt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.zt.model.StockSubscriber;
import com.zt.task.DispatchTask;

import com.zt.util.PropertiesUtil;
import com.zt.util.ServerConfig;

public class ThreadInit {

    private ThreadInit() {
    }

    // 推送线程大小
    private static int threadSize = Integer.parseInt(PropertiesUtil.getConfig(ServerConfig.server_threadSize));

    private static int index = 0;

    // 推送队列
    protected static final List<BlockingQueue<StockSubscriber>> blockingQueues = new ArrayList<>();

    // 初始化消息队列
    public static void initQueue() {
        ExecutorService pool = Executors.newFixedThreadPool(threadSize);
        for (int i = 0; i < threadSize; i++) {
            blockingQueues.add(new LinkedBlockingQueue<>());     //加入8个队列(后面存储要推送的数据)
            pool.execute(new DispatchTask(blockingQueues.get(i)));   //执行8个线程
        }
    }

    // 获取消息队列 TODO 存在并发异常,定时任务是单线程,不存在并发
    public static BlockingQueue<StockSubscriber> getSubQueue() {
        if (index == threadSize) {
            index = 0;
        }
        return blockingQueues.get(index++);
    }

}
