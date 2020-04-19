package com.sdrfengmi.study._008_ThreadPool.threadPoolDome;

public class DefaultThread extends Thread {


    private  BlockingQueue taskQueue = null;

    private boolean isStopped = false;

    public DefaultThread(BlockingQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void run(){
        while(!isStopped() && !Thread.currentThread().isInterrupted()){
            try{
                //从任务队列获取任务并执行
                Runnable runnable = (Runnable) taskQueue.dequeue();
                runnable.run();
            } catch(Exception e){
                isStopped = true;
                break;
            }
        }
    }

    public synchronized void doStop(){
        isStopped = true;
        this.interrupt();
    }

    public synchronized boolean isStopped(){
        return isStopped;
    }
}
