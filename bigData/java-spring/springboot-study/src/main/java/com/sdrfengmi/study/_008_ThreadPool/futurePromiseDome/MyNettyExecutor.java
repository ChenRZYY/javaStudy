package com.sdrfengmi.study._008_ThreadPool.futurePromiseDome;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class MyNettyExecutor implements Executor {

    private ThreadFactory factory;

    public MyNettyExecutor(ThreadFactory factory) {
        this.factory = factory;
    }

    @Override
    public void execute(Runnable command) {
        factory.newThread(command).start();
    }

    // lamd 表达式,只有一个方法的接口
    public static Executor newExecutor() {
        return new MyNettyExecutor(runneber -> {
            return new Thread(runneber);
        });
    }

    public ThreadFactory getFactory() {
        return factory;
    }
}