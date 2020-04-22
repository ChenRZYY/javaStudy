package com.sdrfengmi.study._005_Netty.string;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    private StringBuffer message;

    public ClientHandler(StringBuffer message) {
        this.message = message;
    }

    public String getData() {
        return message.toString();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        Channel channel = channelHandlerContext.channel();
        SocketAddress address = channel.remoteAddress();
        message.append(msg);
        //System.out.println("Client:接收到服务端的消息" + msg);
        //thread.sleep(4000);
        TimeUnit.SECONDS.sleep(5);
        //channel.writeAndFlush(address.toString() + "  client test...........\n");//.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            switch (((IdleStateEvent) evt).state()) {
                case WRITER_IDLE:
                    ctx.channel().writeAndFlush("ping");
                    System.out.println("write timeout");
                    break;
                case READER_IDLE:
                    //ctx.channel().writeAndFlush("");
                    System.out.println("read timeout");
                    ctx.close();
                    break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server disconnect");
        cause.printStackTrace();
        ctx.close();
    }
}
