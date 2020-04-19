package com.zt.model;

import java.util.HashMap;
import java.util.Set;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StockSubscriber {

	private String area;//那一块请求的集合

	private HashMap<String, String> params;//接口请求参数

	private String channelKey;//唯一标识

	private ChannelGroup channels;//用户组
	
	private Set<Channel> channelSet;

	private Integer action;//前端界面请求那个接口

	public StockSubscriber(HashMap<String, String> params, String area, String channelKey) {
		this.params = params;
		this.area = area;
		this.channelKey = channelKey;
		this.action = Integer.valueOf(params.get("Action"));
//		this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	}

	public void add(Channel channel) {
		this.channels.add(channel);
	}

	public void remove(Channel channel) {
		this.channels.remove(channel);
	}
	
	public void addChannel(Channel channel) {
	    this.channelSet.add(channel);
	}
	
	public void removeChannel(Channel channel) {
	    this.channelSet.remove(channel);
	}
}
