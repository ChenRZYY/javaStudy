package com.zt.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zztclient.ClientCallback;
import com.zt.model.StockSubscriber;

import com.zt.service.ClientInit;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestSend {

    // 推送数据
    public static void push(final ConcurrentHashMap<String, HashMap<String, String>> params, final Channel channel) {
        if (params != null && params.size() > 0) {
            //批量请求 入参多Map
            params.forEach((area, data) -> {
                String channelKey = data.containsKey("channelKey") ? data.remove("channelKey") : null;
                StockSubscriber subscriber = new StockSubscriber(data, area, channelKey);
                //              subscriber.add(channel);
                sendData(subscriber, channel);
                // 判断是否需要持续推送,持续推送的放入推送列表
                if (!StringUtil.isNullOrEmpty(channelKey)) {

                    Set<Channel> channelSet = Collections.synchronizedSet(new HashSet<Channel>());
                    channelSet.add(channel);
//                    subscriber.setChannelSet(channelSet); TODO

                    //                  ChannelGroups.putChannel(channelKey, channel); //TODO 
                    subscriber = TimerSend.getPushMap().putIfAbsent(channelKey, subscriber);
                    //已经有推送了,就把channel用户
                    if (subscriber != null) {
//                        subscriber.addChannel(channel); TODO
                    }
                }
            });
        }
    }

    // 调用中焯接口
    public static void sendData(final StockSubscriber subscriber, Channel channel) {
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
                writeMsg(channel, result, channelKey);
            }

            @Override
            public void error(Throwable throwable) {
                log.error("com.zt.util.RequestSend.sendData", throwable);
            }
        });
    }

    // channel 写入消息
    public static void writeMsg(Channel channel, String msg, String channelKey) {

        if (channel != null) {
            //          msg = GZipUtil.gzip(msg);
            //          log.error("返回结果获取数据: "+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date())+" \"channelKey\":\""+channelKey+"\""+msg);
            channel.writeAndFlush(new TextWebSocketFrame(msg)).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("write error", future.cause());
                }
            });
        }
    }

}
