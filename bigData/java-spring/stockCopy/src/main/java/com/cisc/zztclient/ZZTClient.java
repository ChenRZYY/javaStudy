package com.cisc.zztclient;

import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zztclient.ClientCallback;
import com.cisc.zztclient.ClientPoolHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ZZTClient {
    private EventLoopGroup group = new NioEventLoopGroup();
    
    private Bootstrap bootstrap =
        (Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(this.group))
            .channel(NioSocketChannel.class)).option(ChannelOption.SO_KEEPALIVE, true))
                .option(ChannelOption.TCP_NODELAY, true)).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
    
    private int maxConnections = 50;
    
    private ClientPoolHandler handler = new ClientPoolHandler();
    
    private AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool> poolMap =
        new AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool>() {
            
            protected FixedChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(ZZTClient.this.bootstrap.remoteAddress((SocketAddress)key),
                    (ChannelPoolHandler)ZZTClient.this.handler, ZZTClient.this.maxConnections);
            }
        };
    
    private Multimap<String, InetSocketAddress> serverMap = HashMultimap.create();
    
    private Random rand = new Random();
    
    public void registerServer(String type, String ip, int port) {
        this.serverMap.put(type, new InetSocketAddress(ip, port));
    }
    
    public FixedChannelPool getServerPool(String type) {
        Collection<InetSocketAddress> result = this.serverMap.get(type);
        Preconditions.checkState((boolean)(result != null && result.size() > 0), (Object)"No suitable server found.");
        int choose = this.rand.nextInt(result.size());
        InetSocketAddress inetSocketAddress = result.toArray(new InetSocketAddress[0])[choose];
        return (FixedChannelPool)this.poolMap.get(inetSocketAddress);
    }
    
    public void sendData(String type, final ZztMsg data, final ClientCallback callback) {
        final FixedChannelPool pool = this.getServerPool(type);
        // 从连接池拿到连接  Channel channel = this.channelPool.acquire().get();
        // 1 上面拿连接是通过jdk的future,堵塞的方式拿的
        // 2 .addListener通过netty异步方式连接成功以后拿到连接,然后写出数据,连接放回连接池
        pool.acquire().addListener((GenericFutureListener<Future<Channel>>)new FutureListener<Channel>() {
            
            public void operationComplete(Future<Channel> future) throws Exception {
                if (future.isSuccess()) {
                    String HandleSerialNo = UUID.randomUUID().toString();
                    data.setHandleSerialNo(HandleSerialNo);
                    Channel ch = (Channel)future.get();
                    ch.write((Object)data);//写出数据
                    ZZTClient.this.handler.registerSession(HandleSerialNo, callback);
                    ch.flush();//刷新通道
                }
                else {
                    callback.error(future.cause());
                }
                if (future.getNow() != null) {
                    //连接放回连接池,这里一定记得放回去
                    pool.release((Channel)future.getNow());
                }
            }
        });
    }
    
    public void shutdown() {
        this.poolMap.close();
        this.group.shutdownGracefully();
    }
    
}