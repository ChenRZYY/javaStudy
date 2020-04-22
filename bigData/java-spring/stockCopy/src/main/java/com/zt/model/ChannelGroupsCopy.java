package com.zt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChannelGroupsCopy {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static final ConcurrentHashMap<String, List<Channel>> subscribersMap =
            new ConcurrentHashMap<String, List<Channel>>();

    public static void putChannel(String key, Channel channel) {
        List<Channel> channelNew = Collections.synchronizedList(new ArrayList<Channel>());
        channelNew.add(channel);
        List<Channel> channelOld = subscribersMap.putIfAbsent(key, channelNew);
        if (channelOld != null) {
            channelOld.add(channel);
        }
    }

    public static void removeChannel(String key, Channel channel) {
        List<Channel> channels = subscribersMap.get(key);
        if (channels != null) {
            channels.remove(channel);
            if (channels.size() == 0) {
                subscribersMap.remove(key);
            }
        }
    }

}
