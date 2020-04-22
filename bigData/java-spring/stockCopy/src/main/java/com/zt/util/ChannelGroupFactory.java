package com.zt.util;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChannelGroupFactory {

    private static int count = 0;

    private static List<ChannelGroup> groups = new ArrayList<>();

    static {
        groups.add(new DefaultChannelGroup("channelGroup-1", GlobalEventExecutor.INSTANCE));
        groups.add(new DefaultChannelGroup("channelGroup-2", GlobalEventExecutor.INSTANCE));
        groups.add(new DefaultChannelGroup("channelGroup-3", GlobalEventExecutor.INSTANCE));
        groups.add(new DefaultChannelGroup("channelGroup-4", GlobalEventExecutor.INSTANCE));
        groups.add(new DefaultChannelGroup("channelGroup-5", GlobalEventExecutor.INSTANCE));
        groups.add(new DefaultChannelGroup("channelGroup-6", GlobalEventExecutor.INSTANCE));
        groups.add(new DefaultChannelGroup("channelGroup-7", GlobalEventExecutor.INSTANCE));
        groups.add(new DefaultChannelGroup("channelGroup-8", GlobalEventExecutor.INSTANCE));
//        groups.add(new DefaultChannelGroup("channelGroup-9", GlobalEventExecutor.INSTANCE));
//        groups.add(new DefaultChannelGroup("channelGroup-10", GlobalEventExecutor.INSTANCE));
//        groups.add(new DefaultChannelGroup("channelGroup-11", GlobalEventExecutor.INSTANCE));
//        groups.add(new DefaultChannelGroup("channelGroup-12", GlobalEventExecutor.INSTANCE));
//        groups.add(new DefaultChannelGroup("channelGroup-13", GlobalEventExecutor.INSTANCE));
//        groups.add(new DefaultChannelGroup("channelGroup-14", GlobalEventExecutor.INSTANCE));
//        groups.add(new DefaultChannelGroup("channelGroup-15", GlobalEventExecutor.INSTANCE));
//        groups.add(new DefaultChannelGroup("channelGroup-16", GlobalEventExecutor.INSTANCE));
    }

    public synchronized static ChannelGroup getChannelGroup() {
        if (count > 7) {
            count = 0;
        }
        ChannelGroup channelGroup = groups.get(count);
        count++;
        return channelGroup;
    }
}
