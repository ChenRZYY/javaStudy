package com.example.socket.config;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @Author Haishi
 * @create 2020/6/2 20:34
 */
public class Test {


    public static void main(String[] args) {
        try {
            String url = "ws://192.19.23.13:7391";
            URI uri = new URI(url);
            WebSocketClient mWs = new WebSocketClient(uri) {

                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("连接成功");
                }

                @Override
                public void onMessage(String s) {
                    System.out.println(s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {

                }

                @Override
                public void onError(Exception e) {

                }
            };
            mWs.connect();

            while (!mWs.getReadyState().toString().equals("OPEN")) {
                System.out.println(mWs.getReadyState().toString());
                System.out.println("连接中···请稍后");
            }
            mWs.send("");
//            String a = "";
//            mWs.onMessage(a);
            while (true) {
                mWs.send("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
