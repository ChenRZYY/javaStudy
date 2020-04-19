package com.sdrfengmi.study._002_guava_lang3;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

public class GuavaUtils {
    
    /**
     * 
       com.google.common.annotations：普通注解类型。 
            　　com.google.common.base：基本工具类库和接口。 
            　　com.google.common.cache：缓存工具包，非常简单易用且功能强大的JVM内缓存。 
            　　com.google.common.collect：带泛型的集合接口扩展和实现，以及工具类，这里你会发现很多好玩的集合。 
            　　com.google.common.eventbus：发布订阅风格的事件总线。 
            　　com.google.common.hash： 哈希工具包。 
            　　com.google.common.io：I/O工具包。 
            　　com.google.common.math：原始算术类型和超大数的运算工具包。 
            　　com.google.common.net：网络工具包。 
            　　com.google.common.primitives：八种原始类型和无符号类型的静态工具包。 
            　　com.google.common.reflect：反射工具包。 
            　　com.google.common.util.concurrent：多线程工具包。 
            
        Guava集合和不可变对应关系
                        可变集合类型                  可变集合源:JDK/Guava    Guava不可变集合
        Collection-------------JDK------->ImmutableCollection
        List-------------------JDK------->ImmutableList
        Set--------------------JDK------->ImmutableSet
        SortedSet/NavigableSet-JDK------->ImmutableSortedSet
        Map--------------------JDK------->ImmutableMap
        SortedMap--------------JDK------->ImmutableSortedMap
        Multiset--------------Guava------>ImmutableMultiset
        SortedMultiset--------Guava------>ImmutableSortedMultiset
        Multimap--------------Guava------>ImmutableMultimap
        ListMultimap----------Guava------>ImmutableListMultimap
        SetMultimap-----------Guava------>ImmutableSetMultimap
        BiMap-----------------Guava------>ImmutableBiMap  字典类不经常变化
        ClassToInstanceMap----Guava------>ImmutableClassToInstanceMap
        Table-----------------Guava------>ImmutableTable                 多个索引的数据结构集合
    
     * @date 2019年5月17日
     * @author 陈振东
     * @param args
     */
    public static void main(String[] args) {
        
        System.out.println("1_对象为null: " + Objects.isNull(null));
        System.out.println("2_空string判断: " + StringUtils.isNoneEmpty("true"));
        System.out.println("3_string类型jion: " + StringUtils.join("1", "2", "3"));
        
        HashMap<Object, Object> newHashMap = Maps.newHashMap();
    }
    

    
    
}
