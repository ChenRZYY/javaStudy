package com.example.socket.chat;

import com.alibaba.fastjson.JSONObject;
import com.example.socket.cache.CacheService;
import com.example.socket.code.WebScoketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: liaoshiyao
 * @Date: 2019/1/11 16:47
 * @Description: 测试后台websocket客户端
 */
@RestController
@RequestMapping("/websocket")
public class IndexController {

    @Autowired
    private WebScoketServiceImpl webScoketServiceImpl;

    @Autowired
    private CacheService cacheService;

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestBody StockRequest obj) {
        JSONObject jsonObj = (JSONObject) JSONObject.toJSON(obj);
        webScoketServiceImpl.groupSending(jsonObj.toJSONString());
        JSONObject params = jsonObj.getJSONObject("params");
        JSONObject jy_data = params.getJSONObject("jy_data");
        String uuid = jy_data.getString("area");
        String s = webScoketServiceImpl.get(uuid, String.class);
        return s;
    }
}
