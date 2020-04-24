package com.zt.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 启动类配置
 */
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel e) throws Exception {
        // Http消息编码解码
        e.pipeline().addLast("http-codec", new HttpServerCodec());
        //  e.pipeline().addLast("http-request-decoder", new HttpRequestDecoder());
        //  e.pipeline().addLast("http-response-encoder", new HttpResponseEncoder());

        // http消息组装
        e.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        // WebSocket通信支持
        e.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        //5s内没有和服务器端通信的客户端将断开连接
//		e.pipeline().addLast(new ReadTimeoutHandler(5));
//		e.pipeline().addLast("ping", new IdleStateHandler(5, 15, 10,TimeUnit.SECONDS));  

        // 在管道中添加收数据实现方法
        e.pipeline().addLast("handler", new StockWebSocketServerHandler());
    }

}
