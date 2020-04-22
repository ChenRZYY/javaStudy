package com.sdrfengmi.study._005_Netty.future;

/*Future接口**/
public interface MyFuture<V> {
    //是否完成
    boolean isDone();

    //同步方法,返回Future
    MyFuture<V> sync() throws InterruptedException;

    //增加监听器,完成后进行回调
    MyFuture<V> addListener(MyFutureListener<? extends MyFuture<? super V>> listener);


}