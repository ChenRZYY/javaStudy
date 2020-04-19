package com.sdrfengmi.study._008_ThreadPool.threadPoolDome;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool implements ThreadPoolService {

    /**
     * 初始化线程数量
     */
    private final int initSize;

    /**
     * 线程池最大线程数量
     */
    private final int maxSzie;

    /**
     * 线程池核心线程数量
     */
    private final int coreSize;

    /**
     * 当前活跃的线程数量
     */
    private volatile int activeCount;

    private final long keepAliveTime;

    private final TimeUnit timeUnit;

    private InternalTask internalTask;

    /**
     * 创建线程所需的工厂
     */
    private final ThreadFactory threadFactory;

    /**
     * 任务队列，用来存储提交的任务
     */
    private BlockingQueue<Runnable> taskQueue = null;

    /**
     * 线程池中存储线程的容器。
     */
    private Queue<ThreadTask> threads = new ArrayDeque<ThreadTask>();


    /**
     * 默认线程工厂
     */
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new DefaultThreadFactory();

    private boolean isShutdown = false;

    private static volatile Integer threadNameCount = 0;

    /**
     * 默认使用丢弃策略
     */
    private final static DenyPolicy DEFAULT_DENY_POLICY = new DenyPolicy.DiscardDenyPolicy();


    public ThreadPool(int initSize, int maxSize, int coreSize, int maxNoOfTasks) {
        this(initSize, maxSize, coreSize, DEFAULT_THREAD_FACTORY, maxNoOfTasks, DEFAULT_DENY_POLICY, 10, TimeUnit.SECONDS);
    }

    public ThreadPool(int initSize, int maxSize, int coreSize, ThreadFactory threadFactory, int maxNoOfTasks
            , DenyPolicy<Runnable> denyPolicy, long keepAliveTime, TimeUnit timeUnit) {

        this.initSize = initSize;
        this.maxSzie = maxSize;
        this.coreSize = coreSize;
        this.threadFactory = threadFactory;
        this.taskQueue = new BlockingQueue<Runnable>(maxNoOfTasks, DEFAULT_DENY_POLICY, this);
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        init();
    }


    private void init() {
        //初始化线程池
        for (int i = 0; i < initSize; i++) {
            newThread();
        }
        //启动内部维护线程
        internalTask = new InternalTask();
        internalTask.start();
    }

//    private void newThread() {
//        DefaultThread defaultThread = new DefaultThread(taskQueue);
////        System.err.println("newThread" + defaultThread.getName());
//        Thread thread = DEFAULT_THREAD_FACTORY.createThread(defaultThread);
//        System.err.println("newThread" + thread.getName());
//        ThreadTask threadTask = new ThreadTask(thread, defaultThread);
//        activeCount++;
//        threads.add(threadTask);
//        thread.start();
//    }

    private void newThread() {
        DefaultThread defaultThread = new DefaultThread(taskQueue);
        defaultThread.setName("陈振东" + threadNameCount);
        threadNameCount++;
        System.err.println("newThread--" + defaultThread.getName());
//        Thread thread = DEFAULT_THREAD_FACTORY.createThread(defaultThread);
//        System.err.println("newThread" + thread.getName());
        ThreadTask threadTask = new ThreadTask(null, defaultThread);
        activeCount++;
        threads.add(threadTask);
        defaultThread.start();
    }

    private void removeThread() {
        //从线程池中移除某个线程
        ThreadTask threadTask = null;
//        threadTask = threads.remove();
        threadTask = threads.element();
        Thread.State state = threadTask.defaultThread.getState();
        if (!state.equals(Thread.State.TIMED_WAITING)) {
            try {
                System.err.println("移除线程" + threadTask.defaultThread.getName());
                threadTask.defaultThread.interrupt(); //stop 已经不推荐
                threads.remove();
                this.activeCount--;
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("移除线程失败报异常------------------------" + threadTask.defaultThread.getName());
            }
        }
    }


    //    synchronized
    @Override
    public void execute(Runnable task) {
        if (this.isShutdown) {
            throw new IllegalStateException("ThreadPool is stopped");
        }
        //任务入列
        taskQueue.enqueue(task);
    }

    @Override
    public int getInitSize() {
        if (isShutdown) {
            throw new IllegalStateException("The thread pool is destory");
        }
        return this.initSize;
    }

    @Override
    public int getMaxSize() {
        if (isShutdown) {
            throw new IllegalStateException("The thread pool is destory");
        }
        return this.maxSzie;
    }

    @Override
    public int getCoreSize() {
        if (isShutdown) {
            throw new IllegalStateException("The thread pool is destory");
        }
        return this.coreSize;
    }

    @Override
    public int getQueueSize() {
        if (isShutdown) {
            throw new IllegalStateException("The thread pool is destory");
        }
        return taskQueue.size();
    }

    @Override
    public int getActiveCount() {
        synchronized (this) {
            return this.activeCount;
        }
    }

    @Override
    public synchronized void shutdown() {
        this.isShutdown = true;
        threads.forEach(threadTask -> threadTask.defaultThread.doStop());
        internalTask.interrupt();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }


    class InternalTask extends Thread {
        @Override
        public void run() {
            //run方法继承自Thread,主要用于维护线程数量，比如扩容，回收等工作
            while (!isShutdown && !isInterrupted()) {
                try {
                    timeUnit.sleep(1);
//                    System.err.println("循环判断线程多少" + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    isShutdown = true;
                    break;
                }
//                synchronized (InternalTask.this) {
                if (isShutdown) {
                    break;
                }
                //当前队列中任务尚未处理，并且activeCount< coreSize则继续扩容
                if (taskQueue.size() > 0 && activeCount < coreSize) {
                    for (int i = initSize; i < coreSize; i++) {
                        newThread();
                    }
                    //continue的目的在于不想让线程的扩容直接打到maxsize
                    continue;
                }

                //当前的队列中有任务尚未处理，并且activeCount < maxSize则继续扩容
                if (taskQueue.size() > 0 && activeCount < maxSzie) {
                    for (int i = coreSize; i < maxSzie; i++) {
                        newThread();
                    }
                }

                //如果任务队列中没有任务，则需要回收，回收至coreSize即可
                if (taskQueue.size() == 0 && activeCount > coreSize) {
                    for (int i = coreSize; i < activeCount; i++) {
                        removeThread();
                    }
                }
//                }
            }
        }
    }


    /**
     * 工厂模式屏蔽对象创建的过程
     */
    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger GROUP_COUNTER = new AtomicInteger(1);

        private static final ThreadGroup group = new ThreadGroup("customThreadPool-" + GROUP_COUNTER.getAndDecrement());

        private static final AtomicInteger COUNTER = new AtomicInteger(0);

        @Override
        public Thread createThread(Runnable runnable) {
            return new Thread(group, runnable, "thread-pool-" + COUNTER.getAndDecrement());
        }
    }

    /**
     * ThreadTask 只是PoolThread和Thread的组合,因为后面关闭线程还需要用到poolThread的doStop方法
     */
    private static class ThreadTask {
        Thread thread;
        DefaultThread defaultThread;

        public ThreadTask(Thread thread, DefaultThread defaultThread) {
            this.thread = thread;
            this.defaultThread = defaultThread;
        }
    }


    public Queue<ThreadTask> getThreads() {
        return threads;
    }
}
