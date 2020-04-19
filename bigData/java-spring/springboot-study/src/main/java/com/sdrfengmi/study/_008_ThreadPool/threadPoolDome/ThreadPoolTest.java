package com.sdrfengmi.study._008_ThreadPool.threadPoolDome;

import java.util.concurrent.TimeUnit;

/**
 *  线程池的用法
 *
 *  单例,多例对象 -->单线程,多线程 锁的共享与设置
 *  .wait() :对象锁，是使用一个object 对象，将这个对象供多个线程共享使用，然后再线程中，对这个对象加锁。
 */
public class ThreadPoolTest {

    public static void main(String[] args) throws InterruptedException {

        final ThreadPool threadPool = new ThreadPool(2, 10, 4, 15);

        //定义20个任务并且提交到线程池
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        System.out.println(Thread.currentThread().getName() + " is running add done" + finalI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println(Thread.currentThread().getName() + "线程正在运行 强行关闭 InterruptedException----------------------");
                    }

                }
            });

//            threadPool.execute(() ->{
//                try {
//                    TimeUnit.SECONDS.sleep(10);
//                    System.out.println(Thread.currentThread().getName() + " is running add done");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
        }

        while (true) {
            System.out.println(Thread.currentThread().getName() + "shibushimian");
            System.out.println("getActiveCount: " + threadPool.getActiveCount());
            System.out.println("任务数量getQueueSize: " + threadPool.getQueueSize());
            System.out.println("getCoreSize: " + threadPool.getCoreSize());
            System.out.println("getMaxSize: " + threadPool.getMaxSize());
            System.out.println("getMaxSize: " + threadPool.getMaxSize());
            System.out.println("现在线程池中线程的数量: " + threadPool.getThreads().size());
//            System.out.println("======================================");
            TimeUnit.SECONDS.sleep(1);
//            threadPool.shutdown();
//            Thread.currentThread().join();
        }
    }
}
