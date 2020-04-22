package com.client.handler;

/**
 * @Author Haishi
 * @create 2020/3/17 10:44
 */

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    //处理服务端返回的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接受到server响应数据: " + msg);

        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String txt = new String(bytes, "UTF-8");
        System.out.println("Now is:" + txt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);

//        URI url = new URI("/test");
//        String meg = "hello";
//
//        //配置HttpRequest的请求数据和一些配置信息
//        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, url.toASCIIString(), Unpooled.wrappedBuffer(meg.getBytes("UTF-8")));
//        request.headers()
//                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8")
//                //开启长连接
//                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
//                //设置传递请求内容的长度
//                .set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes())
//                .set(HttpHeaders.Names.UPGRADE, "websocket");
//
//        //发送数据
//        ctx.writeAndFlush(request);

        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame("chenzhendong");
        ctx.writeAndFlush(textWebSocketFrame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


}
