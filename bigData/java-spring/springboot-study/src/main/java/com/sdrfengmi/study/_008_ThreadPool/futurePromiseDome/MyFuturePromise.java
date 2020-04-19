package com.sdrfengmi.study._008_ThreadPool.futurePromiseDome;

/*promise接口**/
public interface MyFuturePromise<V> extends MyFuture<V> {

    boolean trySuccess();

    boolean isDone();

    MyFuture sync() throws InterruptedException ;

    @Override
    MyFuturePromise<V> addListener(MyFutureListener<? extends MyFuture<? super V>> listener);

}