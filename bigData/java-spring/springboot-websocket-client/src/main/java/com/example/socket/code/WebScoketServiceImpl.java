package com.example.socket.code;

import com.example.socket.cache.CacheConfig;
import com.example.socket.cache.CacheService;
import com.example.socket.config.WebSocketConfig;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: 陈振东
 * @Date: 2019/1/12 10:56
 * @Description:
 */
@Component
@Slf4j
public class WebScoketServiceImpl implements WebSocketService {

    @Autowired
    private WebSocketClient webSocketClient;

//    @Autowired
//    private WebSocketConfig webSocketConfig;

    @Autowired
    private CacheService cacheService;

    @Override
    public void groupSending(String message) {
        // 这里我加了6666-- 是因为我在index.html页面中，要拆分用户编号和消息的标识，只是一个例子而已
        // 在index.html会随机生成用户编号，这里相当于模拟页面发送消息
        if (!webSocketClient.isOpen()) {
            try {
                webSocketClient.reconnectBlocking();
                int count = 0;
                while (!webSocketClient.getReadyState().toString().equals("OPEN") && count < 30) {
                    System.out.println(webSocketClient.getReadyState().toString());
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("连接中···请稍后");
        }
        webSocketClient.send(message);
    }

    @Override
    public void appointSending(String name, String message) {
        // 这里指定发送的规则由服务端决定参数格式
        webSocketClient.send("");
    }


    @Override
    public void send(String message) {
        if (!webSocketClient.isOpen()) {
            try {
                resetConnect();
                int count = 0;
                while (!webSocketClient.getReadyState().toString().equalsIgnoreCase("OPEN") && count < 50) {
//                    log.info(("连接中···请稍后"));
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        String jsonStr = JSONObject.toJSONString(message);
        // 这里指定发送的规则由服务端决定参数格式
        webSocketClient.send(message);
    }

    /**
     * 3s 没有获取到数据 返回的为空
     *
     * @param uuid
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String uuid, Class<T> clazz) {
        T cache = null;
        int count = 0;
        while (cache == null && count < 30) {
            cache = cacheService.getCache(CacheConfig.Caches.stockInfo.name(), uuid, clazz);
            ++count;
            try {
                if (cache == null) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (cache != null) {
            cacheService.deleteCache(CacheConfig.Caches.stockInfo.name(), uuid);
        }
        return cache;
    }


    public synchronized void resetConnect() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("-------------------重新连接--------------------------");
                    Thread.sleep(1000);
                    if (!webSocketClient.isOpen()) {
                        webSocketClient.reconnect();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
