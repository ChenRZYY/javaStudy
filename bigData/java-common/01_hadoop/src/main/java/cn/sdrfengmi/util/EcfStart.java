package cn.sdrfengmi.util;

import com.jcraft.jsch.JSch;
import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.IOptionName;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import org.junit.Test;

/**
 * @Author chenzhendong
 * @create 2020/6/2 09:59
 */
@SuppressWarnings("all")
public class EcfStart {


    @Test
    public void ecf() throws Exception {

        com.jcraft.jsch.Logger logger = new SettleLogger();
        JSch.setLogger(logger);

        //连接linux配置
        ConnBean connBean = new ConnBean("10.137.36.49", "root", " Cjis8888");
//        ConnBean connBean = new ConnBean("10.137.36.47", "root", " Cjis8888");
//        ConnBean connBean = new ConnBean("10.137.36.37", "root", " Cjis@8888");
//        ConnBean connBean = new ConnBean("47.105.158.112", "root", "zd521707@");
        SSHExec.setOption(IOptionName.SSH_PORT_NUMBER, 22);
        //SSH连接对象
        SSHExec ecfssh = SSHExec.getInstance(connBean);
        //打开连接
        ecfssh.connect();
        // 设置登陆超时时间，不设置可能会报错
        CustomTask task1 = new ExecCommand(
                "cd /opt/services/ecftob",
                "source /etc/profile",
                "ps -ef|grep ecf");

        Result result = ecfssh.exec(task1);
        out(result);

    }

    public static void out(Result result) {
        if (result.isSuccess) {
            System.out.println("Return code: " + result.rc);
            System.out.println(result.sysout);
        } else {
            System.out.println("Return code: " + result.rc);
            System.out.println("error message: " + result.error_msg);
        }
    }
}
