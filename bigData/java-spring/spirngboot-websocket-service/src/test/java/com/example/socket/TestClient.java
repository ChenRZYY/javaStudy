package com.example.socket;

import com.example.socket.util.RemoteExecuteCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author Haishi
 * @create 2020/6/8 13:51
 */

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class TestClient {


    @Test
    public void connect() {
        //        ConnBean connBean = new ConnBean("47.105.158.112", "root", "zd521707@");

        String ip = "47.105.158.112";
//        String ip = "10.137.36.49";
        String username = "root";
        String password = "zd521707@";
        // 设置登陆超时时间，不设置可能会报错
        //CustomTask task1 = new ExecCommand(
        //"cd /opt/services/ecftob",
        //"source /etc/profile",
        //"ps -ef|grep ecf");

        String cmd = "ps -ef|grep ecf";
        String charset = "utf-8";
        String ret = RemoteExecuteCommand.sendCommand(ip, username, password, cmd, charset);
        System.out.println(ret);

    }
}
