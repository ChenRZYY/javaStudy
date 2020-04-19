package com.sdrfengmi.study._005_Netty.string;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientBoot {

    private StringBuffer message = new StringBuffer();

    private Bootstrap bootstrap;

    public ClientBoot() {
        bootstrap = new Bootstrap();
    }

    private String connect(String host, int port) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            bootstrap.group(worker).channel(NioSocketChannel.class)
                    //.option(ChannelOption.SO_TIMEOUT, 3000)
                    //.option(ChannelOption.TCP_NODELAY, true)
                    //.option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //防止TCP粘包，自定义分隔符
                            //ByteBuf buf = Unpooled.copiedBuffer("$".getBytes());
                            pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            //编码和解码
                            pipeline.addLast(new StringDecoder()).addLast(new StringEncoder())
                                    .addLast(new IdleStateHandler(4,0,0))//5s内没有和服务器端通信的客户端将断开连接
                                    .addLast(new ClientHandler(message));
                        }
                    });
            Channel channel = bootstrap.connect(host, port).sync().channel();
            if (channel != null && channel.isActive()) {
                System.out.println("连接成功。。。。。。。。。。。。。。");
                ChannelFuture future = channel.writeAndFlush("test1$\n");
                future.channel().closeFuture().await();
                System.out.println("已返回数据===================================");

            } else {
                System.out.println("连接失败。。。。。。。。。。。。。。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
        return message.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        ClientBoot clientBoot = new ClientBoot();
        String reponse = clientBoot.connect("127.0.0.1", 10000);
        System.out.println(reponse + "--------------------------");
        //Channel channel2 = clientBoot.connect("127.0.0.1", 10000);

        //channel1.writeAndFlush("test1$\n");
        //channel2.writeAndFlush("test2$");
        //channel1.closeFuture();
        //channel1.close();
        //System.exit(0);
        //channel2.writeAndFlush("5s过后$");

    }
}


