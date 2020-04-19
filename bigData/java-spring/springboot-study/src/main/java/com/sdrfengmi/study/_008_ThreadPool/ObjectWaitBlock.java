package com.sdrfengmi.study._008_ThreadPool;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * synchronized()方法类似于操作系统概念中的互斥内存块，在JAVA中的Object类型中，都是带有一个内存锁的，在有线程获取该内存锁后，其它线程无法访问该内存，从而实现JAVA中简单的同步、互斥操作。
 * synchronized(this)与synchronized(static XXX)的区别了，synchronized就是针对内存区块申请内存锁，this关键字代表类的一个对象，所以其内存锁是针对相同对象的互斥操作，而static成员属于类专有，其内存空间为该类所有成员共有，这就导致synchronized()对static成员加锁，相当于对类加锁，也就是在该类的所有成员间实现互斥，在同一时间只有一个线程可访问该类的实例。
 *
 * 如果需要在线程间相互唤醒的话就需要借助Object.wait(), Object.nofity()了
 *
 * Obj.wait()，与Obj.notify()必须要与synchronized(Obj)一起使用，也就是wait,与notify是针对已经获取了Obj锁进行操作，从语法角度来说就是Obj.wait(),Obj.notify必须在synchronized(Obj){...}语句块内。
 * 从功能上来说wait就是说线程在获取对象锁后，主动释放对象锁，同时本线程休眠。直到有其它线程调用对象的notify()唤醒该线程，才能继续获取对象锁，并继续执行。
 * 相应的notify()就是对对象锁的唤醒操作。但有一点需要注意的是notify()调用后，并不是马上就释放对象锁的，而是在相应的synchronized(){}语句块执行结束，自动释放锁后，JVM会在wait()对象锁的线程中随机选取一线程，赋予其对象锁，唤醒线程，继续执行。这样就提供了在线程间同步、唤醒的操作。
 * Thread.sleep()与Object.wait()二者都可以暂停当前线程，释放CPU控制权，主要的区别在于Object.wait()在释放CPU同时，释放了对象锁的控制。
 *
 *
 * 下面都是对象锁,必须先获取锁才能对锁进行下面操作必须在 synchronized (b)
 *  void notify()    唤醒在此对象监视器上等待的单个线程。
 *  void notifyAll() 唤醒在此对象监视器上等待的所有线程。
 *  void wait()      导致当前的线程等待，直到其他线程调用此对象的 notify() 方法或 notifyAll() 方法。
 * 根据jdk的void notifyAll()的描述，“解除那些在该对象上调用wait()方法的线程的阻塞状态。该方法只能在同步方法或同步块内部调用。
 * 如果当前线程不是对象所得持有者，该方法抛出一个java.lang.IllegalMonitorStateException 异常”
 *
 * Condition类的await()、signal()和signalAll()，一般是配合Lock一起使用，是显式的线程间协调同步操作类。每个Lock中可以有多个Condition，如notEmpty、notFull等。
 * 这些方法都是在某个具体的Condition条件队列中调用，唤醒的时候也是类似，使用对应的Condition来唤醒一个或者多个等待的线程。和wait()、notify()类似，使用这些方法时也需要先通过Lock获取锁，await()方法同样会释放锁，并挂起当前线程，等待被通知唤醒去重新竞争锁。
 *
 * synchronized (ObjectWaitBlock.class) 是类锁,该类每次只能有一个线程持有
 *
 */
public class ObjectWaitBlock {

    public static void main(String[] args) throws InterruptedException {

//        private  static volatile  ReentrantLock lock = new ReentrantLock();
//        private  static volatile  Condition condition =lock.newCondition();

        Object b = new Object();
        Object c = new Object();

        Thread thread_0 = new Thread(new BuyFood(b, c));
        thread_0.start();
        Thread.sleep(1000); //TODO 如果不用sleep 可能会死锁,一个拿到b,一个拿到c相互等待
        Thread thread_1 = new Thread(new CookFood(b, c));
        thread_1.start();

        thread_0.join();
        synchronized (b) {
            b.notify();
        }
        //最后c.wait() 没有人唤醒一直处于等待
    }
}

class BuyFood implements Runnable {

    private Object b;

    private Object c;

    public BuyFood(Object b, Object c) {
        this.b = b;
        this.c = c;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            synchronized (c) {
                try {
                    System.out.println("买萝卜" + Thread.currentThread().getName());
//                Thread.sleep(1000); TODO 睡眠1秒,会发生死锁
                    synchronized (b) {
                        p("买萝卜" + i);
                        b.notify();
                    }

                    c.wait();//持有该对象锁的线程等待,释放锁,等被叫醒以后重新获取锁
                    System.out.println("wait买萝卜" + i);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void p(String s) {
        System.out.println(s);
    }

}


class CookFood implements Runnable {

    private Object b;

    private Object c;

    public CookFood(Object b, Object c) {
        this.b = b;
        this.c = c;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            synchronized (b) {
                System.out.println("煮萝卜" + Thread.currentThread().getName());
                //得到买的锁
                synchronized (c) {
                    p("煮萝卜" + i);
                    c.notify();
                }
                try {
                    b.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    private void p(String s) {
        System.out.println(s);
    }

}

