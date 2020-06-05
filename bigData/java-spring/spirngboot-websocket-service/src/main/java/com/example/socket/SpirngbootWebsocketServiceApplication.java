package com.example.socket;

import com.example.socket.code.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
@Slf4j
public class SpirngbootWebsocketServiceApplication {

    @Autowired
    private WebSocket webSocket;

    public static void main(String[] args) {
        SpringApplication.run(SpirngbootWebsocketServiceApplication.class, args);
    }


    @RequestMapping("{name}/**")
    public void dispatcher(@PathVariable String name, HttpServletRequest request) {
        log.debug("根据服务名查询Session");
        WebSocket socket = webSocket.getSessionMap().get(name);

        log.debug("未找到服务，抛出异常"+socket);
        if (socket == null) {
            throw new RuntimeException("找不到该服务实例");
        }
    }

}

