package com.sdrfengmi.study._008_ThreadPool.futurePromiseDome;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class MyExecutorTest {

    public static void main(String[] args) throws InterruptedException {
        MyFuture future = asyncHello().addListener((MyFutureListener<MyFuturePromiseImpl<Void>>) future1 -> System.out.println("监听到完成"));
        if (future.isDone()) {
            System.out.println("异步执行完成");
        } else {
            try {
                future.sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static MyFuture asyncHello() throws InterruptedException {
        Executor executor = MyNettyExecutor.newExecutor();
        final MyFuturePromiseImpl promise = new MyFuturePromiseImpl();

        executor.execute(() -> {
            System.out.println("Hello Async");
            try {
                //模拟一些操作
                Thread.sleep(2000);
                System.err.println(Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promise.trySuccess();
        });

        System.err.println(Thread.currentThread().getName());
        Thread.sleep(2000);
        return promise;
    }
}