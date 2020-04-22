package com.sdrfengmi.study._005_Netty.future;

/*promise接口**/
public interface MyFuturePromise<V> extends MyFuture<V> {

    boolean trySuccess();

    boolean isDone();

    MyFuture sync() throws InterruptedException ;

    @Override
    MyFuturePromise<V> addListener(MyFutureListener<? extends MyFuture<? super V>> listener);

}