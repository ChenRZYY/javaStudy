package com.sdrfengmi.study._006_thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings({"rawtypes", "unused"})
public class CallableFuture {

    public static void main(String[] args) {
        /**
         *
         * https://www.cnblogs.com/hongdada/p/6122437.html 
         * https://blog.csdn.net/aboy123/article/details/38307539
         submit内部调用execute
         submit有返回值
         submit方便exception处理
         submit的demo:

         Runnable和Callable的区别是，
         (1)Callable规定的方法是call(),Runnable规定的方法是run().
         (2)Callable的任务执行后可返回值，而Runnable的任务是不能返回值得
         (3)call方法可以抛出异常，run方法不可以
         (4)运行Callable任务可以拿到一个Future对象，表示异步计算的结果。它提供了检查计算是否完成的方法，以等待计算的完成，并检索计算的结果。通过Future对象可以了解任务执行情况，可取消任务的执行，还可获取执行结果。

         根据结果是否需要,判断是实现Runnable方法,还是实现Callable
         */
        System.err.println(Thread.currentThread().getName());
        //创建一个线程池
        //创建三个有返回值的任务
        ExecutorService execut = Executors.newFixedThreadPool(3);
        execut.execute(new RunnableTest());

        Future<String> future1 = execut.submit(new CallableTest("我是线程1"));
        Future future2 = execut.submit(new CallableTest("我是线程2"));
        Future future3 = execut.submit(new CallableTest("我是线程3"));

        execut.execute(new RunnableTest());
        execut.execute(new RunnableTest());


        try {
            Object object = future1.get();//如果拿不到 这个方法会堵塞
            while (future1.get() == null) {
                System.out.println("还没有完成任务");
            }

            System.out.println(future1.get().toString());
            System.out.println(future2.get().toString());
            System.out.println(future3.get().toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        execut.shutdown();

    }
}

/**
 * @author 陈振东
 * 用callable形式获取结果,这是回调
 */
class CallableTest implements Callable<String> {

    private String threadName;

    public CallableTest(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public String call() throws Exception {
        Thread currentThread = Thread.currentThread();
        Thread.sleep(20000);
        System.out.println(threadName + "返回的信息" + currentThread.getName());
        return threadName + "返回的信息" + currentThread.getName();
    }
}

/**
 * @author 陈振东
 * 用Runnable的形式
 */
class RunnableTest implements Runnable {

    @Override
    public void run() {
        System.err.println(this);
    }

}