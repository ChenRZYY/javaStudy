package com.sdrfengmi.csdn;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * @author:
 * @description:
 * @date: 2019-05-30 23:48
 */
public class Tool {
    public static String doGet(String url) {
        String body = "";
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            //HttpEntity httpEntity = httpResponse.getEntity();
            //body = EntityUtits.toString(httpEntity, Consts.UTF_8);
            //释放连接
            httpGet.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }
}

