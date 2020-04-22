package com.client;

import com.client.handler.WebSocketClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.URI;

public class ClientMain {

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap boot = new Bootstrap();
        boot.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(group)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new ChannelHandler[]{new HttpClientCodec(),
                                new HttpObjectAggregator(1024 * 1024 * 10)});
                        p.addLast("hookedHandler", new WebSocketClientHandler());
                    }
                });

        URI websocketURI = new URI("ws://localhost:7391/ws");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //进行握手
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        System.out.println("connect");
        final Channel channel = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
        WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();


        TextWebSocketFrame frame = new TextWebSocketFrame("{\n" +
                "\t\"params\": {\n" +
                "\t\t\"zsStateData\": {\n" +
                "\t\t\t\"ReqlinkType\": \"0\",\n" +
                "\t\t\t\"Action\": \"60\",\n" +
                "\t\t\t\"AccountIndex\": \"9\",\n" +
                "\t\t\t\"DeviceType\": 0,\n" +
                "\t\t\t\"Direction\": 1,\n" +
                "\t\t\t\"Grid\": \"1A0001,2A01,399006,000001\",\n" +
                "\t\t\t\"Lead\": 1,\n" +
                "\t\t\t\"MaxCount\": 5,\n" +
                "\t\t\t\"NewMarketNo\": 0,\n" +
                "\t\t\t\"NEEDCHECK\": \"1|2|4|3|13|50|11|16|32|242\",\n" +
                "\t\t\t\"StartPos\": 0,\n" +
                "\t\t\t\"StockIndex\": 1,\n" +
                "\t\t\t\"newindex\": 1,\n" +
                "\t\t\t\"tztshowprocess\": 1,\n" +
                "\t\t\t\"channelKey\": \"zsStateData\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}");

        channel.writeAndFlush(frame).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("text send success");
                } else {
                    System.out.println("text send failed  " + channelFuture.cause().getMessage());
                }
            }
        });

        /*
        Thread text = new Thread(new Runnable() {
            public void run() {
                int i = 30;
                while (i > 0) {
                    System.out.println("text send");
                    TextWebSocketFrame frame = new TextWebSocketFrame("我是文本");
                    channel.writeAndFlush(frame).addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                System.out.println("text send success");
                            } else {
                                System.out.println("text send failed  " + channelFuture.cause().getMessage());
                            }
                        }
                    });
                }

            }
        });

        Thread bina = new Thread(new Runnable() {
            public void run() {
                File file = new File("C:\\Users\\Administrator\\Desktop\\test.wav");
                FileInputStream fin = null;
                try {
                    fin = new FileInputStream(file);
                    int len = 0;
                    byte[] data = new byte[1024];
                    while ((len = fin.read(data)) > 0) {
                        ByteBuf bf = Unpooled.buffer().writeBytes(data);
                        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(bf);
                        channel.writeAndFlush(binaryWebSocketFrame).addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                if (channelFuture.isSuccess()) {
                                    System.out.println("bina send success");
                                } else {
                                    System.out.println("bina send failed  " + channelFuture.cause().toString());
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        text.start();
        bina.start();*/
    }
}
