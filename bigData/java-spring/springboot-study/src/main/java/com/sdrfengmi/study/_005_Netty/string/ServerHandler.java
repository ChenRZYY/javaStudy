package com.sdrfengmi.study._005_Netty.string;

import java.net.SocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    int count = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {


        Channel channel = channelHandlerContext.channel();
        SocketAddress address = channel.remoteAddress();
        System.out.println("Server:接收到客户端发来的消息: " + msg);
        channel.writeAndFlush("server test...........\n");
        //channelHandlerContext.close();
        /*if(count < 1){
            count ++;
            channel.writeAndFlush("server test...........\n");
        }*/
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client disconnect");
        cause.printStackTrace();
        ctx.close();
    }
}
