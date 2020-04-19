package com.sdrfengmi.study._008_ThreadPool.futurePromiseDome;

/*Future接口**/
public interface MyFuture<V> {
 
    boolean isDone();
 
    MyFuture<V> sync() throws InterruptedException ;
 
    MyFuture<V> addListener(MyFutureListener<? extends MyFuture<? super V>> listener);
 
 
}