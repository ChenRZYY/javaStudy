package com.zt.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.alibaba.fastjson.JSON;
import com.zt.model.StockServer;
import com.zt.model.StockSubscriber;
import com.zt.task.PushTask;

import io.netty.util.internal.StringUtil;

public class ServerUtil {

	private ServerUtil() {
	}

	// 推送线程大小
	private static int threadSize = Integer.parseInt(PropertiesUtil.getConfig("server.threadSize"));

	private static int index = 0;

	// 服务器信息
	private static String serverMsg = null;

	// 推送队列
	protected static final List<BlockingQueue<StockSubscriber>> blockingQueues = new ArrayList<>();

	// 初始化消息队列
	public static void initQueue() {
		ExecutorService pool = Executors.newFixedThreadPool(threadSize);
		for (int i = 0; i < threadSize; i++) {
			blockingQueues.add(new LinkedBlockingQueue<>());     //加入8个队列(后面存储要推送的数据)
			pool.execute(new PushTask(blockingQueues.get(i)));   //执行8个线程
		}
	}

	// 获取消息队列 TODO 存在并发异常,定时任务是单线程,不存在并发
	public static BlockingQueue<StockSubscriber> getSubQueue() {
		if (index == threadSize) {
			index = 0;
		}
		return blockingQueues.get(index++);
	}

	// 获取服务器信息
	public static String getServerMsg() {
		if (StringUtil.isNullOrEmpty(serverMsg)) {
			initServerMsg();
		}
		return serverMsg;
	}

	// 初始化服务器信息
	public static void initServerMsg() {
		Map<String, Object> resultMap = new HashMap<>();
//		Properties properties = PropertiesUtil.readAll(PropertiesUtil.INPATH_SERVER);
		Properties properties = PropertiesUtil.outInit(PropertiesUtil.OUTPATH_SERVER);
		properties.forEach((k, v) -> {
			String[] serverStr = v.toString().split(";");
			List<StockServer> servers = new ArrayList<>();
			for (String serverMsg : serverStr) {
				String[] msgArr = serverMsg.split(",");
				StockServer server = new StockServer();
				server.setDomain(msgArr[0]);
				server.setName(msgArr[1]);
				if (msgArr.length>2) {
					server.setType(msgArr[2]);
				}
				servers.add(server);
				resultMap.put(k.toString(), servers);
			}
		});
		resultMap.put("area", "message");
		serverMsg = JSON.toJSONString(resultMap);
	}
	
}
