package stock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.alibaba.fastjson.JSON;
import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zztclient.ClientCallback;
import com.cisc.zztclient.ZZTClient;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StudyTest {

	public static void main(String[] args) {

		ZztMsg msg = new ZztMsg();

		ZZTClient client = new ZZTClient();
		// client.registerServer("zt","10.137.35.2", 8886);//中焯测试环境
		client.registerServer("zt", "192.168.1.145", 7776);// 中焯线上 --行情
		// client.registerServer("zt","192.168.1.146", 7779);//中焯线上 --行情

		ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

		client.sendData("zt", msg, new ClientCallback() {
			@Override
			public void call(Object obj) {
				ZztMsg m = (ZztMsg) obj;
				Map<String, Object> map = new HashMap<>();
				m.forEach((k, v) -> map.put(k, v));
				map.put("area", "");
				String result = JSON.toJSONString(map);
				String channelKey = "";

				ChannelGroupFuture writeAndFlush = channels.writeAndFlush(new TextWebSocketFrame("msg"));

				GenericFutureListener<? extends Future<? super Void>> a = null;
				writeAndFlush.addListener(future -> {
				});

				channels.writeAndFlush(new TextWebSocketFrame("msg")).addListener(future -> {
					if (!future.isSuccess()) {
						log.error("write error", future.cause());
					}
				});

				writeAndFlush.addListener(new GenericFutureListener() {

					@Override
					public void operationComplete(Future future) throws Exception {
						System.err.println();
						future.isSuccess();
					}
				}

				);
			}

			@Override
			public void error(Throwable throwable) {
				log.error("sendData error", throwable);
			}
		});

		// List<String> list = new ArrayList<>();
		// write( () -> System.out.println("In Java8!") );
	}

	public void write(List<String> list) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Before Java8 ");
			}
		}).start();

		// Java 8 way:
		new Thread(() -> System.out.println("In Java8!")).start();

	}


}
