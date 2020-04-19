package com.zt.util;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.cisc.zztclient.ZZTClient;
import com.zt.task.DispatchTask;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientUtil {

	private ClientUtil() {

	}

	private static ZZTClient client = null;
	private static String serverType = "";

	public static void init(String type) {
		serverType = StringUtil.isNullOrEmpty(type) ? "quotes" : type;
		switch (serverType) {
		case "trade":
			init("stock.trade_url", "stock.trade_port");
			break;
		case "information":
			init("stock.information_url", "stock.information_port");
			break;
		default:
			initPushJob2();
			init("stock.quotes_url", "stock.quotes_port");
			break;
		}
	}

	// 初始化消息推送JOB
		public static void initPushJob() {
			try {
				JobDetail job = JobBuilder.newJob(DispatchTask.class).build();
				CronTrigger trigger = TriggerBuilder.newTrigger()
						.withSchedule(CronScheduleBuilder.cronSchedule(PropertiesUtil.getConfig("server.push"))).build();
				Scheduler scheduler = new StdSchedulerFactory().getScheduler();
				scheduler.scheduleJob(job, trigger);
				scheduler.start();
				// 初始化推送队列
				ServerUtil.initQueue();
			} catch (Exception e) {
				log.error("PUSH JOB INIT FAILD", e);
			}
		}
		
	// 初始化消息推送JOB、读取两个任务时间(能精确到分钟级别)
	public static void initPushJob2() {
		try {
			//因为一个cron表达式表达不出,用两个表达式
			JobDetail job = JobBuilder.newJob(DispatchTask.class).build();
			JobDetail job2 = JobBuilder.newJob(DispatchTask.class).build();

			CronTrigger trigger = TriggerBuilder.newTrigger()
					.withSchedule(CronScheduleBuilder.cronSchedule(PropertiesUtil.getConfig("server.push"))).build();
			
			CronTrigger trigger2 = TriggerBuilder.newTrigger()
					.withSchedule(CronScheduleBuilder.cronSchedule(PropertiesUtil.getConfig("server.push2"))).build();
			
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.scheduleJob(job, trigger);
			scheduler.scheduleJob(job2,trigger2);
			
			scheduler.start();
			// 初始化推送队列
			ServerUtil.initQueue();
		} catch (Exception e) {
			log.error("PUSH JOB INIT FAILD", e);
		}
	}

	public static void init(String ipkey, String portkey) {
		String ips = PropertiesUtil.getConfig(ipkey);
		String ports = PropertiesUtil.getConfig(portkey);
		String[] ipArry = ips.split(",");
		String[] portArry = ports.split(",");
		client = new ZZTClient();
		//配置多个ip、port端口 连接、随机请求查询
		for (int i = 0; i < ipArry.length; i++) {
			client.registerServer("zt", ipArry[i], Integer.parseInt(portArry[i]));
		}
		
	}
	public static ZZTClient getClient() {
		if (client == null) {
			init(serverType);
		}
		return client;
	}

}
