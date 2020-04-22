package com.sdrfengmi.study._005_Netty.string;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerBoot {

    private int serverPort;

    public ServerBoot(int serverPortrt) throws InterruptedException {
        this.serverPort = serverPortrt;
        bind();
    }

    private void bind() throws InterruptedException {
        //接收客户端连接
        EventLoopGroup boss = new NioEventLoopGroup();
        //和客户端进行通信（读写）
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        //绑定两个线程组
        bootstrap.group(boss, worker)
                //指定无阻塞（NIO）模式
                .channel(NioServerSocketChannel.class);
        /**
         * 对于ChannelOption.SO_BACKLOG的解释：
         * 服务器端TCP内核维护有两个队列，我们称之为A、B队列。客户端向服务器端connect时，会发送带有SYN标志的包（第一次握手），服务器端
         * 接收到客户端发送的SYN时，向客户端发送SYN ACK确认（第二次握手），此时TCP内核模块把客户端连接加入到A队列中，然后服务器接收到
         * 客户端发送的ACK时（第三次握手），TCP内核模块把客户端连接从A队列移动到B队列，连接完成，应用程序的accept会返回。也就是说accept
         * 从B队列中取出完成了三次握手的连接。A队列和B队列的长度之和就是backlog。当A、B队列的长度之和大于ChannelOption.SO_BACKLOG时，
         * 新的连接将会被TCP内核拒绝。所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，导致新的客户端无法连接。
         * 要注意的是，backlog对程序支持的连接数并无影响，backlog影响的只是还没有被accept取出的连接
         *
         *
         * 这个都是socket的标准参数，并不是netty自己的。
         *
         * 具体为：
         * ChannelOption.SO_BACKLOG, 1024
         * BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
         *
         * ChannelOption.SO_KEEPALIVE, true
         * 是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。
         *
         * ChannelOption.TCP_NODELAY, true
         * 在TCP/IP协议中，无论发送多少数据，总是要在数据前面加上协议头，同时，对方接收到数据，也需要发送ACK表示确认。为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。这里就涉及到一个名为Nagle的算法，该算法的目的就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
         *
         * TCP_NODELAY就是用于启用或关于Nagle算法。如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。

         */
        //设置TCP缓冲区
        bootstrap.option(ChannelOption.SO_BACKLOG, 128)
                //.option(ChannelOption.SO_TIMEOUT, 1000)
                //通过NoDelay禁用Nagle，使得消息立即发出去，不用等到一定的数据量才发出去
                //.option(ChannelOption.TCP_NODELAY, true)
                //保持长连接状态
                //.childOption(ChannelOption.SO_KEEPALIVE, true)
                //.option(ChannelOption.SO_SNDBUF, 32 * 1024) //设置发送数据缓冲大小
                //.option(ChannelOption.SO_RCVBUF, 32 * 1024) //设置接受数据缓冲大小

                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //防止TCP粘包，自定义分隔符
                        //ByteBuf buf = Unpooled.copiedBuffer("$".getBytes());
                        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                                //编码和解码
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                //.addLast(new ReadTimeoutHandler(5))//5s内没有和服务器端通信的客户端将断开连接
                                .addLast(new ServerHandler());
                    }
                });

        //调用sync()表示当服务启动成功之后才执行后面的操作
        ChannelFuture future = bootstrap.bind(serverPort).sync();
        if (future.isSuccess()) {
            System.out.println(serverPort + "  server start ........................");
            future.channel().closeFuture().sync();
            System.out.println("server closed ........................");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ServerBoot serverBoot1 = new ServerBoot(10000);
        //ServerBoot serverBoot2 = new ServerBoot(10001);
    }
}
