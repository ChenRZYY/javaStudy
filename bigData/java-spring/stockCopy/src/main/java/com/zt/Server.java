package com.zt;

import com.zt.handler.ChildChannelHandler;
import com.zt.util.ClientUtil;
import com.zt.util.PropertiesUtil;
import com.zt.util.ServerUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {

    public static void main(String[] args) {
        // 判断服务器类型,初始化client
        ClientUtil.init((args == null || args.length == 0) ? null : args[0]);
        run();
    }

    public static void run() {
        //启动一个spring上下文环境
        log.info("==========netty服务端启动==========");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // 初始化服务器信息
//            ServerUtil.initServerMsg();
            // 启动netty服务器
            int port = Integer.valueOf(PropertiesUtil.getConfig("server.port"));
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup);
            b.channel(NioServerSocketChannel.class);
            //b.childOption(ChannelOption.SO_KEEPALIVE, true);
            //b.option(ChannelOption.SO_BACKLOG, 128);//缓冲区大小
            b.childHandler(new ChildChannelHandler());
            log.info("服务端开启等待客户端连接 ... ...");
            ChannelFuture cf = b.bind(port).sync();
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("RUN ERROR", e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
