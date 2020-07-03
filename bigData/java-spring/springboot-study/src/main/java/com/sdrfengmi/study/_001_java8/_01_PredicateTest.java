package com.sdrfengmi.study._001_java8;

import org.junit.Test;

//https://www.cnblogs.com/rever/tag/Function/
public class _01_PredicateTest {
    //    Predicate的源码跟Function的很像，我们可以对比这两个来分析下。直接上Predicate的源码：


    @Test
    public void byteStr() {
        byte[] bytes = "00".getBytes();
        byte[] bytes2 = "陈振东".getBytes();
        byte a = bytes[0];
        byte b = bytes[1];
        byte c = 96;
        byte d = '0';
        byte f = '0';
//        byte p= d+f;
        Byte sex = new Byte("00");
        byte p = sex;
        new String();

        System.out.println(a + b);
    }
}
