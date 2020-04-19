package com.sdrfengmi.study._008_ThreadPool.futurePromiseDome;

import java.util.EventListener;

/*listener接口，提供complete方法**/
public interface MyFutureListener<F extends MyFuture<?>> extends EventListener {

    void operationComplete(F future);
}
