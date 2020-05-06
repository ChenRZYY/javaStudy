package com.zt.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 服务端心跳包 处理
 * 每个channel都有一个心跳类
 */
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {
    private int lossConnectCount = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("已经5秒未收到客户端的消息了！" + this);
        if (evt instanceof IdleStateEvent) { //直接就是 IdleStateEvent 不加判断也行
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    lossConnectCount++; // 读空闲的计数加1
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    super.userEventTriggered(ctx, evt); //可以不写
                    // 不处理
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    super.userEventTriggered(ctx, evt); //可以不写
                    // 不处理
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "超时事件：" + eventType);
            if (lossConnectCount > 3) {
                System.out.println(" [server]读空闲超过3次，关闭连接");
                ctx.channel().writeAndFlush("you are out");
                ctx.channel().close();
            }
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        lossConnectCount = 0;
        System.out.println("client says: " + msg.toString());
        if ("I am alive".equals(msg.toString())) {
            ctx.channel().writeAndFlush("copy that");
        } else {
            System.out.println(" 其他信息处理 ... ");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}