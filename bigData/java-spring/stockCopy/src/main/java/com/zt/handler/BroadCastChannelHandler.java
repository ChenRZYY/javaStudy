package com.zt.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.spi.Message;
import com.zt.model.ChannelGroups;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelMatcher;

/**
 * 比如多 实例 ChannelInboundHandler 处理器
 */
public class BroadCastChannelHandler extends ChannelInboundHandlerAdapter {

//    private static final Gson GSON = new GsonBuilder().create();

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final AtomicInteger response = new AtomicInteger();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        if (ChannelGroups.size() > 0) {
            Message msg = new Message(ch.remoteAddress().toString().substring(1), SDF.format(new Date()));
            ChannelGroups.broadcast("channelActive", new ChannelMatchers());
        }
        ChannelGroups.add(ch);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (ChannelGroups.contains(ch) && String.valueOf(msg).equals("welcome")) {
            System.out.println(String.format("receive [%s] from [%s] at [%s]", String.valueOf(msg),
                    ch.remoteAddress().toString().substring(1), SDF.format(new Date())));
            response.incrementAndGet();
        }
        synchronized (response) {
            System.out.println(response.get() + "\t" + ChannelGroups.size());
            if (response.get() == ChannelGroups.size() - 1) {
                System.out.println("server close all connected channel");
                ChannelGroups.disconnect();
                response.set(0);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ChannelGroups.discard(ctx.channel());
        response.decrementAndGet();
    }

    public static class ChannelMatchers implements ChannelMatcher {

        @Override
        public boolean matches(Channel channel) {
            return true;
        }

    }

}