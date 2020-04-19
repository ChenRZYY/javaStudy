package com.zt.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String cancelAreas;//取消推送:一块区域channelKey(可以传多块channelKeys) 取消推送区域 1,2,3

	private String message;//返回站点:不为null,说明要返回站点信息

	private ConcurrentHashMap<String, HashMap<String, String>> params;//接收前端任意数据类型 key:area代表界面上那一块,value:请求的入参

}
