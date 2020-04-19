package com.sdrfengmi.study._008_ThreadPool;

/**
 * ThreadA->ThreadB->ThreadC->ThreadA循环执行三个线程。为了控制线程执行的顺序，那么就必须要确定唤醒、等待的顺序，所以每一个线程必须同时持有两个对象锁，才能继续执行。
 * 一个对象锁是prev，就是前一个线程所持有的对象锁。还有一个就是自身对象锁。主要的思想就是，为了控制执行的顺序，必须要先持有prev锁，也就前一个线程要释放自身对象锁，再去申请自身对象锁，两者兼备时打印，
 * 之后首先调用self.notify()释放自身对象锁，唤醒下一个等待线程，再调用prev.wait()释放prev对象锁，终止当前线程，等待循环结束后再次被唤醒。运行上述代码，可以发现三个线程循环打印ABC，共10次。
 * 程序运行的主要过程就是A线程最先运行，持有C,A对象锁，后释放A,C锁，唤醒B。线程B等待A锁，再申请B锁，后打印B，再释放B，A锁，唤醒C，线程C等待B锁，再申请C锁，后打印C，再释放C,B锁，唤醒A。看起来似乎没什么问题，
 * 但如果你仔细想一下，就会发现有问题，就是初始条件，三个线程按照A,B,C的顺序来启动，按照前面的思考，A唤醒B，B唤醒C，C再唤醒A。
 * 但是这种假设依赖于JVM中线程调度、执行的顺序。具体来说就是，在main主线程启动ThreadA后，需要在ThreadA执行完，在prev.wait()等待时，再切回线程启动ThreadB，ThreadB执行完，在prev.wait()等待时，再切回主线程，启动ThreadC，只有JVM按照这个线程运行顺序执行，才能保证输出的结果是正确的。而这依赖于JVM的具体实现。考虑一种情况，如下：如果主线程在启动A后，执行A，过程中又切回主线程，启动了ThreadB,ThreadC，之后，由于A线程尚未释放self.notify，也就是B需要在synchronized(prev)处等待，而这时C却调用synchronized(prev)获取了对b的对象锁。这样，在A调用完后，同时ThreadB获取了prev也就是a的对象锁，ThreadC的执行条件就已经满足了，会打印C，之后释放c,及b的对象锁，这时ThreadB具备了运行条件，会打印B，也就是循环变成了ACBACB了。这种情况，可以通过在run中主动释放CPU
 */
public class ThreadWaitTest2 implements Runnable {

    private String name;
    private Object prev;
    private Object self;

    private ThreadWaitTest2(String name, Object prev, Object self) {
        this.name = name;
        this.prev = prev;
        this.self = self;
    }

    @Override
    public void run() {
        int count = 10;
        while (count > 0) {
            synchronized (prev) {
                synchronized (self) {
                    System.out.print(name);
                    count--;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    self.notify();
                }
                try {
                    prev.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) throws Exception {
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        ThreadWaitTest2 pa = new ThreadWaitTest2("A", c, a);
        ThreadWaitTest2 pb = new ThreadWaitTest2("B", a, b);
        ThreadWaitTest2 pc = new ThreadWaitTest2("C", b, c);

        //如果不用sleep方法,会变成打印 ACB
        // 1 因为        A对象先获取-->c  a锁-->释放a锁 唤醒a对象锁的其他线程
        // 2 这时候只能  C对象获取-->b锁 等待c锁 -->等待上面的1执行完成以后获取C锁
        // 3             B对象显示等到1释放a锁以后 再等待C对象释放b锁
        //最后就是打印A C B  加上休眠以后 会按照顺序获取锁 ABC 打印
        new Thread(pa).start();
        Thread.sleep(10);
        new Thread(pb).start();
        Thread.sleep(10);
        new Thread(pc).start();
        Thread.sleep(10);
    }
}