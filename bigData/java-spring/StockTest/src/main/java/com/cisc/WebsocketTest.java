package com.cisc;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

public class WebsocketTest extends Thread {
    
    private List<String> parameters;
    
    private String connect ;
    
    private boolean flag ;
    
    private ExecutorService loopPool;
    
    public WebsocketTest(List<String> parameters, String connect, boolean flag,ExecutorService loopPool) {
        super();
        this.parameters = parameters;
        this.connect = connect;
        this.flag = flag;
        this.loopPool = loopPool;
    }
    public WebsocketTest(List<String> parameters, String connect, boolean flag) {
        super();
        this.parameters = parameters;
        this.connect = connect;
        this.flag = flag;
    }
    
    public void run() {
        WebSocketClient client = new WebSocketClient();
        SimpleEchoSocket socket = new SimpleEchoSocket();
        socket.setParameters(parameters);
        socket.setFlag(flag);
//        socket.setLoopPool(loopPool);
        try {
            client.start();
            //          URI uri = new URI("ws://10.137.36.46:7391"); //48行情服务
            //            URI uri = new URI("ws://localhost:7392"); //48行情服务
            URI uri = new URI("ws://" + connect); //48行情服务
            //          URI uri = new URI("ws://127.0.0.1:7391"); //48行情服务
            
            //          URI uri = new URI("ws://q3.gf.com.cn:9443"); //48行情服务
            
            //          URI uri = new URI("ws://127.0.0.1:8888/Quotes-Service/info/ksppgmba/websocket"); //48行情服务
            
            //          URI uri = new URI("ws://192.19.23.7:7391");  //本地启动
            //          URI uri = new URI("ws://192.168.1.67:7391"); //线上环境
            
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, uri, request);
            socket.awaitClose(500000000, TimeUnit.SECONDS);
            
            //            long ran = (long)((Math.random() * 9 + 1) * 100000000000L);
            //            String mac = String.valueOf(ran);
            //            System.out.println("MAC=" + mac);
            //          
            
            //            
            //            //获取验证码
            //          String action41092 = SimpleEchoSocket.getCheckCode(mac);
            //          Future<Void> futCode = session.getRemote().sendStringByFuture(action41092);
            //          futCode.get(2, TimeUnit.SECONDS);
            //          
            //            //获取公钥
            //            String action40141 = SimpleEchoSocket.getPublicKey(mac);
            //            Future<Void> futKey = session.getRemote().sendStringByFuture(action40141);
            //            futKey.get(2, TimeUnit.SECONDS);
            //            
            //            while (MapUtils.isEmpty(SimpleEchoSocket.result.get("41092"))
            //                || MapUtils.isEmpty(SimpleEchoSocket.result.get("40141"))) {
            //                Thread.sleep(500);
            //            }
            //            String checkCode = SimpleEchoSocket.result.get("41092").get("CheckCode");
            //            String checkToken = SimpleEchoSocket.result.get("41092").get("CheckToken");
            //            String publicKey = SimpleEchoSocket.result.get("40141").get("Modulus");
            //            
            //            
            //            String login = SimpleEchoSocket.login(checkCode, checkToken, publicKey);
            //            session.getRemote().sendStringByFuture(login);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                client.stop();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
}
