package com.sdrfengmi.study._004_test;

//import org.junit.Test;

public class UnitTest {
    
    //单元测试
    
//    @Test
    public void test1() {
        Thread t = Thread.currentThread();
        
        ThreadGroup group = t.getThreadGroup();
        
        ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();
//        Thread thread = new Thread(threadLocal);
    }
    
}
