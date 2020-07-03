package com.example.socket.code;

import com.example.socket.util.TailLogThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: liaoshiyao
 * @Date: 2019/1/11 11:48
 * @Description: websocket 服务类
 */

/**
 * @ServerEndpoint 这个注解有什么作用？
 * <p>
 * 这个注解用于标识作用在类上，它的主要功能是把当前类标识成一个WebSocket的服务端
 * 注解的值用户客户端连接访问的URL地址
 */

@Slf4j
@Component
@ServerEndpoint("/websocket/{name}")
public class WebSocket {

    /**
     * 与某个客户端的连接对话，需要通过它来给客户端发送消息
     */
    private Session session;

    /**
     * 用于存所有的连接服务的客户端，这个对象存储是安全的
     */
    private static ConcurrentHashMap<String, WebSocket> webSocketSet = new ConcurrentHashMap<>();

    private Process process;
    private InputStream inputStream;

    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "name") String name) {
        this.session = session;
        webSocketSet.put(name, this); //每个人生成一个 WebSocket
        log.info("[WebSocket] 连接成功，当前连接人数为：={}", webSocketSet.size());

        if ("chen".equals(name)) {
            // 执行tail -f命令
            try {
                process = Runtime.getRuntime().exec("tail -f 10 /opt/services/ecftob/logs/EcfTradeToBServicev10/ecfTrade_all.log");
                inputStream = process.getInputStream();

                // 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
                TailLogThread thread = new TailLogThread(inputStream, session);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClose
    public void onClose(@PathParam(value = "name") String name) {
        webSocketSet.remove(this);
        log.info("[WebSocket] 退出成功，当前连接人数为：={}", webSocketSet.size());
    }

    @OnError
    public void onError(@PathParam(value = "name") String name, Throwable throwable) {
        log.error("连接发生错误 {}", throwable.getMessage());
        webSocketSet.remove(name);
    }


    @OnMessage
    public void OnMessage(String message) {
        log.info("[WebSocket] 收到消息：{}", message);
        if (message.indexOf("TOUSER") == 0) {
            String name = message.substring(message.indexOf("TOUSER") + 6, message.indexOf(";"));
            AppointSending(name, message.substring(message.indexOf(";") + 1, message.length()));
        } else {
            GroupSending(message);
        }

    }

    /**
     * 群发
     *
     * @param message
     */
    public void GroupSending(String message) {
        for (String name : webSocketSet.keySet()) {
            try {
                webSocketSet.get(name).session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 指定发送
     *
     * @param name
     * @param message
     */
    public void AppointSending(String name, String message) {
        try {
            webSocketSet.get(name).session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<String, WebSocket> getSessionMap() {
        return webSocketSet;
    }
}
