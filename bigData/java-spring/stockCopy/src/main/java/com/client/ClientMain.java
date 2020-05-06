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
import io.netty.handler.timeout.IdleStateHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class ClientMain {

    private Channel channelRest;
    private NioEventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap boot;

    private final String ip = "localhost";
    private final Integer port = 7391;
    private final Integer maxCount = 10; //尝试连接次数

    public static void main(String[] args) throws Exception {

        ClientMain clientMain = new ClientMain();
        int count = 1;
        int start = 30080;
        for (int i = start; i < start + count; i++) {
            clientMain.run(i); //可以监听每个对象里面的channel
        }
        clientMain.doCon(clientMain);

    }

    public void doCon(ClientMain clientMain) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                        if (channelRest == null || !channelRest.isActive()) {
                            clientMain.run(30080);
                            System.out.println("---------监听中---------------");
//                            wait();//线程等待
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(runnable).start();
//        notifyAll();
    }

    public void run(int port) throws Exception {
        boot = new Bootstrap();
        //1 服务配置
        boot.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(group) //线程池
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new ChannelHandler[]{
                                new HttpClientCodec(),  //http请求
                                new HttpObjectAggregator(1024 * 1024 * 10)
                        });
                        p.addLast(new IdleStateHandler(0, 0, 5)); // 监听每个channel 设置客户端的机制为 读0,写0,读写5 秒触发一次ping

                        p.addLast("hookedHandler", new WebSocketClientHandler()); //handler处理器
                    }
                });

        //        Channel channel1 = boot.bind(port).sync().channel();//为什么不能bind端口

        //2 进行握手
        channelRest = connect(boot);
//         Channel channel = connect(boot);

        //3 发送数据
        sendData(channelRest);


        // TODO 判断channel是否连接上  可以用一个线程定时检测,自动重连
        //        while (channel.isActive()) {
        //            channel.writeAndFlush("陈振东");
        //        }

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

    private static void sendData(Channel channel) {
        //3 发送数据
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
    }

    private static Channel connect(Bootstrap boot) throws URISyntaxException, InterruptedException {
        URI websocketURI = new URI("ws://localhost:7391/ws");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        System.out.println("connect");
        final Channel channel = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
        handshaker.handshake(channel); //像服务端发送握手
        //获取 handler
        WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);
        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();
        return channel;
    }


    /**
     * 连接服务端 and 重连,重连次数
     */
    protected void doConnect(int count) {
        final int attemptCount = count++;
        if (count > maxCount || (channelRest != null && channelRest.isActive())) {
            return;
        }
        ChannelFuture connect = boot.connect(ip, port);
        //实现监听通道连接的方法
        connect.addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture channelFuture) throws Exception {

                if (channelFuture.isSuccess()) {
                    channelRest = channelFuture.channel();
                    System.out.println("连接成功");
                } else {
                    System.out.println("每隔2s重连....");
                    channelFuture.channel().eventLoop().schedule(new Runnable() {

                        public void run() {
                            // TODO Auto-generated method stub
                            doConnect(attemptCount);
                        }
                    }, 2, TimeUnit.SECONDS);
                }
            }
        });
    }


}
