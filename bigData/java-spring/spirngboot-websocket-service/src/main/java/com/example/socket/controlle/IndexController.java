package com.example.socket.controlle;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: liaoshiyao
 * @Date: 2019/1/11 16:47
 * @Description: 测试后台websocket客户端
 */
@RestController
@RequestMapping("/websocket")
public class IndexController {


    @GetMapping("/sendMessage")
    public String sendMessage(String message) {
        if ("gg".equals(message)) {

        }
        return message;
    }
}
