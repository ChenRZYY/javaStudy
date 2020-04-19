package com.zt.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zztclient.ClientCallback;
import com.zt.model.StockSubscriber;
import com.zt.task.GZIPUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StockUtil {
    
    // 推送列表
    private static final ConcurrentHashMap<String, StockSubscriber> subscribersMap =
        new ConcurrentHashMap<String, StockSubscriber>();
    
    public static ConcurrentHashMap<String, StockSubscriber> getPushMap() {
        return subscribersMap;
    }
    
    // 调用中焯接口
    public static void timerSendData(final StockSubscriber subscriber) {
    	//String uuid= UUID.randomUUID().toString().replace("-", "");
        ZztMsg msg = new ZztMsg();
        msg.setAction(subscriber.getAction());
        subscriber.getParams().forEach((k, v) -> msg.putString(k, v));
        
                  //log.error(uuid+" 1 请求中卓"+new SimpleDateFormat(" yyyy/MM/dd-HH:mm:ss:SSS ").format(new Date())+"channelKey:"+subscriber.getChannelKey());
        ClientUtil.getClient().sendData("zt", msg, new ClientCallback() {
            @Override
            public void call(Object obj) {
                ZztMsg m = (ZztMsg)obj;
                Map<String, Object> map = new HashMap<>();
                m.forEach((k, v) -> map.put(k, v));
                map.put("area", subscriber.getArea());
                String result = JSON.toJSONString(map);
                String channelKey = subscriber.getChannelKey();
                //log.error(uuid+" 2 中卓返回"+new SimpleDateFormat(" yyyy/MM/dd-HH:mm:ss:SSS ").format(new Date())+"channelKey:"+subscriber.getChannelKey());

                timerWriteMsg(subscriber.getChannels(), result, channelKey);
            }
            
            @Override
            public void error(Throwable throwable) {
                log.error("com.zt.util.StockUtil.timerSendData zztClient报错", throwable);
            }
        });
    }
    
    
    /**
     * 定时推送消息
     */
    public static void timerWriteMsg(ChannelGroup channels, String msg, String channelKey) {
        
        if (channels != null && !channels.isEmpty()) {
            //          msg = GZipUtil.gzip(msg);
            channels.writeAndFlush(new TextWebSocketFrame(msg)).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("com.zt.util.StockUtil.timerWriteMsg ", future.cause());
                }
				else {
                   // log.error(uuid+" 3 写回前端"+new SimpleDateFormat(" yyyy/MM/dd-HH:mm:ss:SSS ").format(new Date())+"channelKey:"+channelKey);
                }
            });
        }
//        else {
//            if (!StringUtil.isNullOrEmpty(channelKey)) {
//                subscribersMap.remove(channelKey);
//            }
//        }
    }
    
    // 取消推送
    public static void cancel(String channelKeys, Channel channel) {
        String[] area = channelKeys.split(",");
        for (String key : area) {
            StockSubscriber subscriber = subscribersMap.get(key);
            if (subscriber != null) {
                subscriber.remove(channel);
                // 当该股票无推送列表时移除
//                if (subscriber.getChannels().isEmpty()) {
//                    subscribersMap.remove(key);
//                }
            }
        }
    }
    
    // 推送数据
    public static void push(final ConcurrentHashMap<String, HashMap<String, String>> params, final Channel channel) {
        if (params != null && params.size() > 0) {
            //批量请求 入参多Map
            params.forEach((area, data) -> {
                String channelKey = data.containsKey("channelKey") ? data.remove("channelKey") : null;
                
                StockSubscriber subscriber = null;
                if (StringUtil.isNullOrEmpty(channelKey)) {
                    subscriber = new StockSubscriber(data, area, channelKey);
                }
                else if (subscribersMap.containsKey(channelKey)) {
                    subscriber = subscribersMap.get(channelKey);
                    subscriber.add(channel);
                }
                else {
                    ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                    channels.add(channel);
                    subscriber = new StockSubscriber(data, area, channelKey);
                    subscriber.setChannels(channels);
                    subscribersMap.put(channelKey, subscriber);
                }
                sendData(subscriber, channel);
            });
        }
    }
    
    // 调用中焯接口
    public static void sendData(final StockSubscriber subscriber, Channel channel) {
        ZztMsg msg = new ZztMsg();
        msg.setAction(subscriber.getAction());
        subscriber.getParams().forEach((k, v) -> msg.putString(k, v));
        ClientUtil.getClient().sendData("zt", msg, new ClientCallback() {
            @Override
            public void call(Object obj) {
                ZztMsg m = (ZztMsg)obj;
                Map<String, Object> map = new HashMap<>();
                m.forEach((k, v) -> map.put(k, v));
                map.put("area", subscriber.getArea());
                String result = JSON.toJSONString(map);
                //                String channelKey = subscriber.getChannelKey();
                writeMsg(channel, result);
            }
            
            @Override
            public void error(Throwable throwable) {
                log.error("com.zt.util.StockUtil.sendData zztClient报错", throwable);
            }
        });
    }
    
    public static void writeMsg(Channel channel, String msg) {
        
        if (channel.isActive()) {
            //          msg = GZipUtil.gzip(msg);
            //          log.error("返回结果获取数据: "+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date())+" \"channelKey\":\""+channelKey+"\""+msg);
            channel.writeAndFlush(new TextWebSocketFrame(msg)).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("com.zt.util.StockUtil.writeMsg", future.cause());
                }
            });
        }
    }
    
    /**
     * 用压缩二进制流的方式写 channel 写入消息 暂时没用
     */
    public static void writeMsgByBinary(ChannelGroup channels, String msg, String channelKey) {
        
        if (channels != null && !channels.isEmpty()) {
            //          msg = GZipUtil.gzip(msg);
            
            //1 先压缩再转成二进制
            //          String gzipMsg = GZipUtil.gzip(msg);
            
            //2 Gzip 压缩
            byte[] gzip = null;
            try {
                gzip = GZIPUtils.compress(msg);
                //              gzip = msg.getBytes();
            }
            catch (IOException e) {
                log.error("write error", e);
            }
            
            //          String gzipBs64 = GZipUtil.gzip(msg);
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
        }
        else {
            if (!StringUtil.isNullOrEmpty(channelKey)) {
                subscribersMap.remove(channelKey);
            }
        }
    }
}
