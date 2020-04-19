package com.sdrfengmi.study._002_guava_lang3;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;


public class BoardDict {
    private final static String dictName = "BOARD";

    private static BiMap<String, String> dictionary = new ImmutableBiMap.Builder<String, String>()
            .put("0", "深圳交易所")    // 板块Code -> Name
            .put("1", "上海交易所")
            .put("2", "股转系统")
            .build();

    private static BiMap<String, String> getDictionary(){
        return dictionary;
    }

    //字典名称
    public String dictName(){
        return dictName;
    };

    //取字典转换值
    public static String get(String key) {
        return getDictionary().getOrDefault(key, key);
    }

    //取字典反向转换值
    public static String inverse(String key){
        return getDictionary().inverse().getOrDefault(key, key);
    }


}
