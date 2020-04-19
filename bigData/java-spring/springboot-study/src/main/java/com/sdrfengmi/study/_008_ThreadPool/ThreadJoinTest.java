package com.sdrfengmi.study._008_ThreadPool;

import java.util.concurrent.TimeUnit;

/**
 * thread.join的含义是当前线程需要等待previousThread线程终止之后才从thread.join返回。简单来说，就是线程没有执行完之前，会一直阻塞在join方法处。
 * 当前线程执行的任务中要等待该任务中调用了 .join的线程
 *
 * 9->8->7...0->main线程 以此依赖等待, 如果加了if方法,就是等待前一个线程
 *
 * 从join方法的源码来看，join方法的本质调用的是Object中的wait方法实现线程的阻塞，wait方法的实现原理我们在后续的文章再说详细阐述。但是我们需要知道的是，调用wait方法必须要获取锁，所以join方法是被synchronized修饰的，synchronized修饰在方法层面相当于synchronized(this),this就是previousThread本身的实例。
 *
 */
public class ThreadJoinTest extends Thread {
    int i;
    Thread previousThread; //上一个线程

    public ThreadJoinTest(Thread previousThread, int i) {
        this.previousThread = previousThread;
        this.i = i;
    }

    @Override
    public void run() {
        try {
            //调用上一个线程的join方法，大家可以自己演示的时候可以把这行代码注释掉
//            if (i % 2 == 0) {
            TimeUnit.MILLISECONDS.sleep(1000);
            previousThread.join();
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("num:" + i);
    }

    public static void main(String[] args) throws InterruptedException {
        Thread previousThread = Thread.currentThread();
        for (int i = 0; i < 10; i++) {
            ThreadJoinTest threadJoinTest = new ThreadJoinTest(previousThread, i);
            threadJoinTest.start();
            previousThread = threadJoinTest;
        }

        TimeUnit.MILLISECONDS.sleep(1000);
        System.out.println("main 执行完成");
    }
}
