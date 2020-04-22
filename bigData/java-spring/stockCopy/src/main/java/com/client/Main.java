package com.client;

import com.client.handler.NettyClient;
import com.client.protocol.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.net.URI;
import java.util.UUID;

/**
 * client
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        NettyClient client = new NettyClient(host, 7391);
        //启动client服务
        client.start();

        Channel channel = client.getChannel();
        //消息体
        RpcRequest request = new RpcRequest();
        request.setId(UUID.randomUUID().toString());
        request.setData("client.message");
        //channel对象可保存在map中，供其它地方发送消息


        String msg = "{\n" +
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
                "}";
        URI uri = new URI("http://127.0.0.1:7391");
        DefaultFullHttpRequest request2 = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

        // 构建http请求
        request2.headers().set(HttpHeaders.Names.HOST, host);
        request2.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request2.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request2.content().readableBytes());
        request2.headers().set(HttpHeaders.Names.UPGRADE, "websocket");

        //创建一个16字节的buffer,这里默认是创建heap buffer
        ByteBuf buf = Unpooled.buffer(16);
        //写数据到buffer
        for (int i = 0; i < 16; i++) {
            buf.writeByte(i + 1);
        }

//        channel.writeAndFlush(buf).addListener(future -> {
//            if (!future.isSuccess()) {
//                System.err.println("发送数据没有成功" + future.cause());
//            } else {
//                System.err.println("发送数据成功");
//
//            }
//        });

        ByteBuf buf2 = Unpooled.buffer(16);
        //写数据到buffer
        for (int i = 0; i < 16; i++) {
            buf2.writeByte(i + 1);
        }
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame("chenzhendong");
        channel.writeAndFlush(textWebSocketFrame);
    }
}
