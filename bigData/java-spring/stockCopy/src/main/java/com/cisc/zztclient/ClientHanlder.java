package com.cisc.zztclient;

import com.cisc.zzt.msg.ZztMsg;
import com.google.common.cache.Cache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHanlder extends SimpleChannelInboundHandler<ZztMsg> {
    private static final Logger log = LoggerFactory.getLogger(ClientHanlder.class);
    private Cache<String, ClientCallback> sessions;

    public ClientHanlder(Cache<String, ClientCallback> sessions) {
        this.sessions = sessions;
    }

    public void channelRead0(ChannelHandlerContext ctx, ZztMsg msg) throws Exception {
        String serial = msg.getHandleSerialNo();
        ClientCallback cb = (ClientCallback) this.sessions.getIfPresent(serial);
        if (cb != null) {
            this.sessions.invalidate(serial);
            cb.call(msg);
        } else {
            log.error("no request found.error.");
        }

    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }
}