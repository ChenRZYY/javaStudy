package com.zt.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zztclient.ClientCallback;
import com.cisc.zztclient.ZZTClient;
import com.zt.model.StockSubscriber;
//import com.zt.util.GZipUtil;

import com.zt.util.GZIPUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("all")
public class StockSend {

    // 推送列表
    private static final ConcurrentHashMap<String, StockSubscriber> subscribersMap = new ConcurrentHashMap<String, StockSubscriber>();

    public static ConcurrentHashMap<String, StockSubscriber> getPushMap() {
        return subscribersMap;
    }

    // 调用中焯接口
    public static void sendData(final StockSubscriber subscriber) {
        ZztMsg msg = new ZztMsg();
        msg.setAction(subscriber.getAction());
        subscriber.getParams().forEach((k, v) -> msg.putString(k, v));
        ClientInit.getClient().sendData("zt", msg, new ClientCallback() {
            @Override
            public void call(Object obj) {
                ZztMsg m = (ZztMsg) obj;
                Map<String, Object> map = new HashMap<>();
                m.forEach((k, v) -> map.put(k, v));
                map.put("area", subscriber.getArea());
                String result = JSON.toJSONString(map);
                String channelKey = subscriber.getChannelKey();
                writeMsg(subscriber.getChannels(), result, channelKey);
            }

            @Override
            public void error(Throwable throwable) {
                log.error("sendData error", throwable);
            }
        });
    }

    // channel 写入消息
    public static void writeMsg(ChannelGroup channels, String msg, String channelKey) {

        if (channels != null && !channels.isEmpty()) {
//			msg = GZipUtil.gzip(msg);
//			log.error("返回结果获取数据: "+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date())+" \"channelKey\":\""+channelKey+"\""+msg);
            ChannelGroupFuture channelFutures = channels.writeAndFlush(new TextWebSocketFrame(msg)).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("write error", future.cause());
                } else {
                    log.error("write success", future.cause());
                }
            });
            boolean flag = channelFutures.isSuccess();
            System.out.println(flag);
            System.out.println(Thread.currentThread() + Thread.currentThread().getName());

        } else {
            if (!StringUtil.isNullOrEmpty(channelKey)) {
                subscribersMap.remove(channelKey);
            }
        }
    }

    //用压缩二进制流的方式写 channel 写入消息
    public static void writeMsgByBinary(ChannelGroup channels, String msg, String channelKey) {

        if (channels != null && !channels.isEmpty()) {
//			msg = GZipUtil.gzip(msg);

            //1 先压缩再转成二进制
//			String gzipMsg = GZipUtil.gzip(msg);

            //2 Gzip 压缩
            byte[] gzip = null;
            try {
                gzip = GZIPUtils.compress(msg);
//				gzip = msg.getBytes();
            } catch (IOException e) {
                log.error("write error", e);
            }

//			String gzipBs64 = GZipUtil.gzip(msg);
            //转成二进制
            ByteBuf result = Unpooled.buffer();
//            result.writeBytes(gzipMsg.getBytes());
            result.writeBytes(gzip);
//            result.writeBytes(gzipBs64.getBytes());
            log.error("返回结果: " + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()) + msg);
            channels.writeAndFlush(new BinaryWebSocketFrame(result)).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("write error", future.cause());
                }
            });
        } else {
            if (!StringUtil.isNullOrEmpty(channelKey)) {
                subscribersMap.remove(channelKey);
            }
        }
    }

    // 取消推送
    public static void cancel(String channelKeys, Channel channel) {
        String[] area = channelKeys.split(",");
        for (String key : area) {
            StockSubscriber subscriber = subscribersMap.get(key);
            if (subscriber != null) {
                subscriber.remove(channel);
                // 当该股票无推送列表时移除
                if (subscriber.getChannels().isEmpty()) {
                    subscribersMap.remove(key);
                }
            }
        }
    }

    // 推送数据
    public static void push(final ConcurrentHashMap<String, HashMap<String, String>> params, final Channel channel) {
        if (params != null && params.size() > 0) {
            //批量请求 入参多Map
            params.forEach((area, data) -> {
                String channelKey = data.containsKey("channelKey") ? data.remove("channelKey") : null;
                StockSubscriber subscriber = new StockSubscriber(data, area, channelKey);
                subscriber.add(channel);
                sendData(subscriber);
                // 判断是否需要持续推送,持续推送的放入推送列表
                if (!StringUtil.isNullOrEmpty(channelKey)) {
                    subscriber = subscribersMap.putIfAbsent(channelKey, subscriber);
                    //已经有推送了,就把channel用户
                    if (subscriber != null) {
                        subscriber.add(channel);
                    }
                }
            });
        }
    }

}
