package com.example.socket.cache;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;


@Slf4j
@Component
//@Scope("prototype")
public class CacheService {


    @Autowired
    private CacheManager cacheManager;

    @Cacheable(value = "basketConfigInfo", key = "#cuacct_code", unless = "#result==null || #result ==\"\" || #result.size()==0")
    public List<Object> getBasConfInfo(String cuacct_code, String cust_code, String opSite, String branchNo, String session) {
        return null;
    }

    @CachePut(value = "basketConfigInfo", key = "#cuacct_code", unless = "#result==null || #result ==\"\" || #result.size()==0")
    public List<Object> updateBasConfCache(List<Object> rspList, List<Object> hqList, String cuacct_code) {
        //更新默认篮子中的行情数据
        return null;
    }

    @CacheEvict(value = "basketConfigInfo", key = "#cuacct_code")
    public void delBasConfCache(String cuacct_code) {
        //清除默认篮子缓存
    }


    @CacheEvict(value = "stockCombinationInfo", key = "#cuacct_code")
    public void delStockCombCache(String cuacct_code) {
        //清除组合持仓缓存
    }


    @Cacheable(value = "undeSecuInfo", unless = "#result==null || #result ==\"\" || #result.size()==0")
    public JSONArray getUndeSecuInfo(String cust_code, String branch, String opSite, String session, String cuacct_code) {//标的券无需按资金账号缓存
        //缓存标的券
        return null;
    }

    @Cacheable(value = "enableSlInfo", key = "#cuacct_code", unless = "#result==null || #result ==\"\" || #result.size()==0")
    public JSONArray getEnableSlInfo(String cust_code, String branch, String opSite, String session, String cuacct_code) {//可融券按资金账号缓存
        //缓存公司可融券标的
        return null;
    }


    //默认篮子判断融资标的
    public List<Object> writeEnableFiFlag(String cust_code, String branch, String opSite, String session,
                                          List<Object> rspList, String cuacct_code) {
        return null;
    }

    public void saveCache(String name, String key, Object value) {
        if (!StringUtils.isEmpty(key)) {
            Cache cache = cacheManager.getCache(name);
            cache.put(key, value);
        }
    }

    public <T> T getCache(String name, String key, Class<T> type) {
        Cache cache = cacheManager.getCache(name);
        T t = cache.get(key, type);
        return t;
    }

    public void deleteCache(String name, String key) {
        Cache cache = cacheManager.getCache(name);
        cache.evict(key);
    }

}
