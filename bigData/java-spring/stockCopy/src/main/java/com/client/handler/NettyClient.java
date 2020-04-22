package com.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 *
 */
public class NettyClient {

    private final String host;
    private final int port;
    private Channel channel;

    //连接服务端的端口号地址和端口号
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)  // 使用NioSocketChannel来作为连接用的channel类
                .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("正在连接中...");
                        ChannelPipeline pipeline = ch.pipeline();
//                        pipeline.addLast(new RpcEncoder(RpcRequest.class)); //编码request
//                        pipeline.addLast(new RpcDecoder(RpcResponse.class)); //解码response


                        // Http消息编码解码
//                        pipeline.addLast("http-codec", new HttpClientCodec());
                        // http消息组装
//                        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                        // WebSocket通信支持

//                        pipeline.addLast(new ByteArrayToBinaryEncoder());
                        // 在管道中添加收数据实现方法
//                        pipeline.addLast("handler", new StockWebSocketServerHandler());


                        // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
//                        pipeline.addLast(new HttpRequestEncoder());
                        // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
//                        pipeline.addLast(new HttpResponseDecoder());
//                        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
//                        ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
//                        ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
//                        ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
//                        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
//                        pipeline.addLast(new ByteArrayToBinaryEncoder()); //客户端处理类
//                        pipeline.addLast(new ClientHandler()); //客户端处理类


//                        //包含编码器和解码器
//                        pipeline.addLast(new HttpClientCodec());
//
//                        //聚合
//                        pipeline.addLast(new HttpObjectAggregator(1024 * 10 * 1024));
//
//                        //解压
//                        pipeline.addLast(new HttpContentDecompressor());

                        pipeline.addLast(new ChannelHandler[]{new HttpClientCodec(), new HttpObjectAggregator(1024 * 1024 * 10)});
                        pipeline.addLast(new ClientHandler());
                    }
                });
        //发起异步连接请求，绑定连接端口和host信息
        final ChannelFuture future = b.connect(host, port).sync();

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture arg0) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("连接服务器成功");
                } else {
                    System.out.println("连接服务器失败");
                    future.cause().printStackTrace();
                    group.shutdownGracefully(); //关闭线程组
                }
            }
        });

        this.channel = future.channel();
    }

    public Channel getChannel() {
        return channel;
    }
}
