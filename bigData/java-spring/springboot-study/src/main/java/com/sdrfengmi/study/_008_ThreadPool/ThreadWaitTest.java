package com.sdrfengmi.study._008_ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程执行完成后再执行某个方法,进行加锁
 *
 * 通过同一个 waitObject对象锁进行锁定,wait等待,并且有线程是否执行成功count进行标记  每次都唤醒所有线程,线程检查是否执行完成
 */
public class ThreadWaitTest {

    public static void main(String[] args) throws InterruptedException {
        final int threadCount = 10;
        final AtomicInteger count = new AtomicInteger(threadCount); //计数线程是否执行完
        final Object waitObject = new Object();

        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < threadCount; i++) {
            final int j = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("运行线程号:" + j);
                    synchronized (waitObject) {
                        int cnt = count.decrementAndGet();
                        if (cnt == 0) {
                            waitObject.notifyAll();
                        }

                    }
                }
            });
        }

        synchronized (waitObject) {
            while (count.get() != 0) {
                waitObject.wait();
            }
        }

        Thread thread = new Thread();
        thread.run();
        Thread.sleep(1000);
        System.out.println("结束线程");
        System.exit(0);
    }

}

