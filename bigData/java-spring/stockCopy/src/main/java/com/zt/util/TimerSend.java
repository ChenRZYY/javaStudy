package com.zt.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zztclient.ClientCallback;
import com.zt.model.StockSubscriber;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimerSend {
    
    // 推送列表
    private static final ConcurrentHashMap<String, StockSubscriber> subscribersMap =
        new ConcurrentHashMap<String, StockSubscriber>();
    
    public static ConcurrentHashMap<String, StockSubscriber> getPushMap() {
        return subscribersMap;
    }
    
    // 调用中焯接口
    public static void sendData(final StockSubscriber subscriber) {
        
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
                writeMsg(subscriber.getChannelSet(), result, subscriber.getChannelKey());
            }
            
            @Override
            public void error(Throwable throwable) {
                log.error("sendData error", throwable);
            }
        });
    }
    
    // channel 写入消息
    public static void writeMsg(Set<Channel> channels, String msg, String channelKey) {
        
        ChannelGroup channelGroup = ChannelGroupFactory.getChannelGroup();
        synchronized (channelGroup) {
            channelGroup.addAll(channels);
            if (channelGroup != null && !channelGroup.isEmpty()) {
                channelGroup.writeAndFlush(new TextWebSocketFrame(msg)).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("com.zt.util.TimerSend.writeMsg write error", future.cause());
                    }
                });
                //            textWebSocketFrame.release();
            }
            else {
                if (!StringUtil.isNullOrEmpty(channelKey)) {
                    subscribersMap.remove(channelKey);
                }
                //            channels.close();
            }
            
            if (channelGroup.size() != channels.size()) {
                channels.clear();
                System.err.println("set中channel长度为: "+channels.size());
                channels.addAll(channelGroup);
                System.err.println("去除无用的Channel");
                System.err.println("set中channel长度为: "+channels.size());
            }
            channelGroup.clear();
            //      System.err.println(ChannelGroups.channels.size());
        }
//        Iterator<Channel> channels = channelSet.iterator();
//        
//        synchronized (channels) {
//            while (channels.hasNext()) {
//                Channel channel = channels.next();
//                if (channel.isActive()) {
//                    channel.writeAndFlush(new TextWebSocketFrame(msg)).addListener(future -> {
//                        if (!future.isSuccess()) {
//                            log.error("com.zt.util.TimerSend.writeMsg write error", future.cause());
//                        }
//                    });
//                }
//                else {
//                    channelSet.remove(channel);
//                }
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
                // StockWebSocketServerHandler.channels.remove(channel);
                if (subscriber.getChannelSet().isEmpty()) {
                    subscribersMap.remove(key);
                }
            }
        }
    }
    
}
