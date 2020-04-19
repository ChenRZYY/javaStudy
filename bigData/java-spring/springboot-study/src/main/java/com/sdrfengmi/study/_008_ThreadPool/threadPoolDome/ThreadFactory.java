package com.sdrfengmi.study._008_ThreadPool.threadPoolDome;

@FunctionalInterface
public interface ThreadFactory {
    Thread createThread(Runnable runnable);
}
