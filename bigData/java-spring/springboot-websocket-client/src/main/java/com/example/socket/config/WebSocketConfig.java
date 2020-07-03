package com.example.socket.config;

import com.alibaba.fastjson.JSONObject;
import com.example.socket.cache.CacheConfig;
import com.example.socket.cache.CacheService;
import com.example.socket.code.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: liaoshiyao
 * @Date: 2019/1/11 17:38
 * @Description:
 */
@Slf4j
@Component
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Autowired
    private WebSocketService webSocketService;

    public static AtomicInteger atoCount = new AtomicInteger();

//    @Autowired
//    private WebSocketClient webSocketClient;

    @Autowired
    private CacheService cacheService;

    public static int timeout = 604800;//7天

    @Bean
    public WebSocketClient webSocketClient(ServerProperties serverProperties) {
        try {
            log.info("---------------------------------------[websocket] 开始连接 服务url: " + serverProperties.getStockServerUrl() + " ---------------------------------------");
            WebSocketClient webSocketClient = new WebSocketClient(new URI(serverProperties.getStockServerUrl()), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("---------------------------------------[websocket] 连接成功 服务url" + serverProperties.getStockServerUrl() + "---------------------------------------");
                }

                @Override
                public void onMessage(String message) {
//                    log.info("[websocket] 收到消息={}", message);
                    if (message != null && !message.equals("gg")) {
                        //保存行情数据
                        saveStockInfo(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("[websocket] 退出连接");
                    webSocketService.resetConnect(); //有退出延迟1s重连
                }

                @Override
                public void onError(Exception ex) {
                    log.info("[websocket] 连接错误={}", ex.getMessage());
                }
            };
            webSocketClient.setConnectionLostTimeout(timeout);
            webSocketClient.connect();
            return webSocketClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void saveStockInfo(String message) {
        //获取行情数据
        JSONObject jsonObj = JSONObject.parseObject(message);
        String uuid = jsonObj.getString("area");
        String action = jsonObj.getString("action");
        JSONObject stocklist = jsonObj.getJSONObject("stocklist");
        atoCount.incrementAndGet();
        log.info("send:" + TimeTask.atoCount.get() + "save: " + atoCount.get());
        log.info("[websocket] 收到消息={} action: " + action, message);
        cacheService.saveCache(CacheConfig.Caches.stockInfo.name(), uuid, message);
    }


}
