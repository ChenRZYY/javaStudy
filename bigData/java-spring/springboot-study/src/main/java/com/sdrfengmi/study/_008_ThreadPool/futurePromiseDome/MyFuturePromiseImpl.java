package com.sdrfengmi.study._008_ThreadPool.futurePromiseDome;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class MyFuturePromiseImpl<V> implements MyFuturePromise<V> {

    private List<MyFutureListener<? extends MyFuture<?>>> listeners = Lists.newArrayList();

    private volatile Object result = null;

    private volatile Integer waiters = 0;

    //    MyFuturePromise<V> addListener(MyFutureListener<? extends MyFuture<? super V>> listener);
    @Override
    public MyFuturePromise<V> addListener(MyFutureListener<? extends MyFuture<? super V>> listener) {
        synchronized (this) {
            System.err.println(Thread.currentThread().getName());
            if (listeners == null) {
                listeners = new ArrayList<MyFutureListener<? extends MyFuture<?>>>();
                listeners.add(listener);
            } else {
                listeners.add(listener);
            }
        }
        if (isDone()) {
            for (MyFutureListener f : listeners) {
                f.operationComplete(this);
            }
        }
        return this;
    }

    @Override
    public boolean trySuccess() {
        result = new Object();
        checkNotify();
        for (MyFutureListener f : listeners) {
            f.operationComplete(this);
        }
        return true;
    }

    private synchronized void checkNotify() {
        if (waiters > 0) {
            notifyAll();
        }
    }

//    @Override
//    public MyFuturePromise addListener(MyFutureListener<? extends MyFuture> listener) {
//        synchronized (this) {
//            if(listeners == null){
//                listeners = new ArrayList<MyFutureListener<? extends MyFuture<?>>>();
//                listeners.add(listener);
//            }else {
//                listeners.add(listener);
//            }
//        }
//        if (isDone()){
//            for (MyFutureListener f: listeners
//            ) {
//                f.operationComplete(this);
//            }
//        }
//        return this;
//    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public MyFuture<V> sync() throws InterruptedException {
        if (isDone()) {
            return this;
        }
        synchronized (this) {
            while (!isDone()) {
                waiters++;
                try {
                    wait();
                } finally {
                    waiters--;
                }
            }
        }
        return this;
    }


}
