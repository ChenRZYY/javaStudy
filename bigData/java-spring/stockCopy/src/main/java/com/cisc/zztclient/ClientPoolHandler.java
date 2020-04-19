package com.cisc.zztclient;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPoolHandler implements ChannelPoolHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientPoolHandler.class);
    
    private Cache<String, ClientCallback> sessions = CacheBuilder.newBuilder()
        .maximumSize(10000L)
        .expireAfterWrite(60000L, TimeUnit.SECONDS)
        .removalListener(new RemovalListener<String, ClientCallback>() {
            public void onRemoval(RemovalNotification<String, ClientCallback> notification) {
                if (!notification.getCause().equals(RemovalCause.EXPLICIT)) {
                    ClientPoolHandler.log
                        .error("remove:" + notification.getCause().name() + "->" + (String)notification.getKey());
                }
            }
        })
        
        .build();
    
    /**
     * 使用完channel需要释放才能放入连接池
     * @see io.netty.channel.pool.ChannelPoolHandler#channelReleased(io.netty.channel.Channel)
     */
    @Override
    public void channelReleased(Channel ch) throws Exception {
        //刷新管道里的数据
        // ch.writeAndFlush(Unpooled.EMPTY_BUFFER); //flush掉所有写回的数据  
    }
    
    /**
     * 获取连接池中的channel
     * @see io.netty.channel.pool.ChannelPoolHandler#channelAcquired(io.netty.channel.Channel)
     */
    @Override
    public void channelAcquired(Channel ch) throws Exception {
    }
    
    /**
     * 当channel不足时会创建，但不会超过限制的最大channel数
     * @see io.netty.channel.pool.ChannelPoolHandler#channelCreated(io.netty.channel.Channel)
     */
    @Override
    public void channelCreated(Channel ch) throws Exception {
        SocketChannel channel = (SocketChannel)ch;
        channel.pipeline()
        .addLast(new ChannelHandler[]{new LoggingHandler(LogLevel.DEBUG)})
        .addLast(new ChannelHandler[]{new MsgEncoder()})
        .addLast(new ChannelHandler[]{new MsgDecoder()})
        .addLast(new ChannelHandler[]{new ClientHanlder(sessions)});//// 客户端逻辑处理   ClientHandler这个也是咱们自己编写的，继承ChannelInboundHandlerAdapter，实现你自己的逻辑
    }
    
    public void registerSession(String serial, ClientCallback callback) {
        sessions.put(serial, callback);
    }
}