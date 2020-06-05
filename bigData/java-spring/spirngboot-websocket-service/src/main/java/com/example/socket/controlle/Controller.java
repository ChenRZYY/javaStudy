package com.example.socket.controlle;

import com.example.socket.code.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author fengtao.xue
 */
@RestController
public class Controller {

    @Autowired
    WebSocket webSocket;

    /**
     * 群发消息内容
     *
     * @param message
     * @return
     */
    @RequestMapping(value = "/ws/sendAll", method = RequestMethod.GET)
    public String sendAllMessage(@RequestParam(required = true) String message) {
        webSocket.GroupSending(message);
        return "success";
    }

    /**
     * 指定会话ID发消息
     *
     * @param message 消息内容
     * @param name    连接会话ID
     * @return
     */
    @RequestMapping(value = "/ws/sendOne", method = RequestMethod.GET)
    public String sendOneMessage(@RequestParam(required = true) String message,
                                 @RequestParam(required = true) String name) {
        webSocket.AppointSending(name, message);
        return "success";
    }
}