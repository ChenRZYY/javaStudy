package com.cisc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SimpleEchoSocket {
    //	private static final String[] arr = new String[] {
    //			"{\"params\":{\"zsData\":{\"ReqlinkType\":\"0\",\"Action\":\"60\",\"AccountIndex\":\"9\",\"DeviceType\":0,\"Direction\":1,\"Grid\":\"1A0001,2A01,399006\",\"Lead\":1,\"MaxCount\":3,\"NewMarketNo\":0,\"NEEDCHECK\":\"1|2|4|3|13|50|11|16|32|242\",\"StartPos\":0,\"StockIndex\":1,\"newindex\":1,\"tztshowprocess\":1,\"channelKey\":\"zsData\"}},\"cancelAreas\":\"zsData\"}",
    //			};
    
    //行情
    //	private static final String[] arr = new String[] {
    //			"{\"params\":{\"zsData\":{\"ReqlinkType\":\"0\",\"Action\":\"60\",\"AccountIndex\":\"9\",\"DeviceType\":0,\"Direction\":1,\"Grid\":\"1A0001,2A01,399006\",\"Lead\":1,\"MaxCount\":3,\"NewMarketNo\":0,\"NEEDCHECK\":\"1|2|4|3|13|50|11|16|32|242\",\"StartPos\":0,\"StockIndex\":1,\"newindex\":1,\"tztshowprocess\":1,\"channelKey\":\"zsData\"}},\"cancelAreas\":\"zsData\"}",
    //	};
    
    private List<String> parameters = new ArrayList<>();
    
    private boolean flag = false;
    
    private ExecutorService loopPool;

    //交易链接
    //	private static final String[] arr = new String[] {
    //	    "{\"params\": {\"cc\": {\"Action\": \"121\",\"BeginDate\": \"20181101\",\"EndDate\": \"20181201\",\"IntactToServer\": \"@ClZvbHVtZUluZm8JAAAARjg2NS03MzlB\",\"MobileCode\": \"130000000000\",\"ReqlinkType\": \"1\",\"Reqno\": \"1557224290168\",\"StartPos\": \"0\",\"Token\": \"mAVvlS4871@F865-739A671Hr4JiJ\",\"UserCode\": \"24512072\",\"newindex\": \"1\"}}}"
    //,
    //	};
    
    //    private static String[]  arr=new String[] {};
    
    private final CountDownLatch closeLatch;
    
    @SuppressWarnings("unused")
    public static Session session;
    
    public static Map<String, Map<String, String>> result = Maps.newConcurrentMap();
    
    public SimpleEchoSocket() {
        this.closeLatch = new CountDownLatch(1);
    }
    
    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }
    
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown(); // trigger latch
    }
    
    @SuppressWarnings("static-access")
    @OnWebSocketConnect
    public void onConnect(Session session) {
        
        //	    arr=new String[] {login(null,null,null)};
        this.session = session;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Future<Void> fut;
                    while (true) {
                        for (String string : parameters) {
                            fut = session.getRemote().sendStringByFuture(string);
                            fut.get(2, TimeUnit.SECONDS);
                        }
                        if (!flag) {
                            break;
                        }
                        Thread.sleep(3000);
                    }
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
//        loopPool.execute(runnable);
    }
    
    @OnWebSocketMessage
    public void onMessage(String msg) {
        log.error("获取消息时间 " + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()) + msg);
        //		Map<String,Object> msgMap = JSON.parseObject(msg, Map.class);
        //		Object action = msgMap.get("Action").toString();
        //		Map<String, String> newHashMap = Maps.newHashMap();
        //		msgMap.forEach((k,v)->{
        //		    newHashMap.put(k, v.toString());
        //		});
        //		result.put(action.toString(),newHashMap);
    }
    
    public static String getCheckCode(String mac) {
        
        //获取验证码
        Map<String, Map<String, Map<String, String>>> loginMap = Maps.newHashMap();
        Map<String, Map<String, String>> paramMap = Maps.newHashMap();
        Map<String, String> dataMap = Maps.newHashMap();
        loginMap.put("params", paramMap);
        paramMap.put("jy_data", dataMap);
        dataMap.put("Action", "41092");
        dataMap.put("reqlinktype", "1");
        dataMap.put("newindex", "1");
        dataMap.put("mobilecode", mac);
        dataMap.put("terminalcode", "kpt");
        
        String jsonString = JSON.toJSONString(loginMap);
        return jsonString;
    }
    
    public static String getPublicKey(String mac) {
        //获取公钥
        Map<String, Map<String, Map<String, String>>> loginMap = Maps.newHashMap();
        Map<String, Map<String, String>> paramMap = Maps.newHashMap();
        Map<String, String> dataMap = Maps.newHashMap();
        loginMap.put("params", paramMap);
        paramMap.put("jy_data", dataMap);
        dataMap.put("Action", "40141");
        dataMap.put("reqlinktype", "1");
        dataMap.put("newindex", "1");
        dataMap.put("mobilecode", mac);
        dataMap.put("terminalcode", "kpt");
        //          client.callRmote("40141", data00, mac);
        
        String jsonString = JSON.toJSONString(loginMap);
        return jsonString;
    }
    
    public static String login(String checkToken, String checkCode, String publicKey) {
        //数据类型
        String password = RsaUtil.encrypt("123444", publicKey);
        Map<String, Map<String, Map<String, String>>> loginMap = Maps.newHashMap();
        Map<String, Map<String, String>> paramMap = Maps.newHashMap();
        Map<String, String> dataMap = Maps.newHashMap();
        loginMap.put("params", paramMap);
        paramMap.put("jy_data", dataMap);
        dataMap.put("MobileCode", "($MobileCode)");
        dataMap.put("UserCode", "24512072");
        dataMap.put("MobileCode", "($MobileCode)");
        dataMap.put("Reqno", "1558340228947");
        dataMap.put("ReqlinkType", "1");
        dataMap.put("newindex", "1");
        dataMap.put("Action", "100");
        dataMap.put("password", password);
        //        dataMap.put("password", "9f95e4d5640589ae229a8d0497a906c3a18dcb4518bef93ab35eb9564ea7cab1cf6466eeb4b8452e1f764225d40833a5ad98a50e8ad5a964486270211b0267993edcef33435cf5875c76fa4b0b825649df21cbb0b9b6dde58f889efda50ee6019b913e63c6bd4e634bfc49db7ce797643b85715907fd39b592ccde91e579972e");
        dataMap.put("account", "24512072");
        dataMap.put("accounttype", "KHBH");
        dataMap.put("modulus_id", "0");
        dataMap.put("CheckToken", checkToken);
        dataMap.put("CheckCode", checkCode);
        dataMap.put("maxcount", "100");
        dataMap.put("yybcode", "14080");
        String jsonString = JSON.toJSONString(loginMap);
        return jsonString;
    }
    
    public List<String> getParameters() {
        return parameters;
    }
    
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
    
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    
    public boolean getFlag() {
        return flag;
    }
    public void setLoopPool(ExecutorService loopPool) {
        this.loopPool = loopPool;
    }
    public ExecutorService getLoopPool() {
        return loopPool;
    }
    
}
