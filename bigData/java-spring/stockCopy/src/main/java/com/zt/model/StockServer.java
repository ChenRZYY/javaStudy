package com.zt.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockServer {

	private String domain;// 域名

	private String port;// 端口

	private String activeCount;// 活动的连接数
	
	private String name;//服务器名称
	
	private String type;//web、client类型标识

}
