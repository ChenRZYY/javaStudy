package cn.sdrfengmi.util;

import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.IOptionName;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import net.neoremind.sshxcute.task.impl.ExecShellScript;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("all")
public class ExcuteShellTest {

    //   private ConnBean connBean =null;
    private SSHExec ssh = null;


    //    这里需要注意一个问题：通过远程连接到linux主机上时，在执行命令时，命令前面已经是他的完全路径，不能是简单的一个命令，否则会出现不认识在该命令。
    //    如　CustomTask sampleTask1 = new ExecCommand(" /home/grid/sqoop-1.4.5/bin/sqoop help")
//    或者在执行该命令之前，执行source /etc/profile。但是要注意这句话要跟所要执行的命令一起放在一起，用分号隔开。
//    如下所示： （如果两个命令不放在一起，将会出现原来同样的错误）CustomTask sampleTask1 = new ExecCommand(" source /etc/profile; sqoop help")
    @Test
    public void example() throws TaskExecFailException {
        //连接linux配置
        ConnBean connBean = new ConnBean("server02", "root", "zd521707@");
        //SSH连接对象
        SSHExec ssh = SSHExec.getInstance(connBean);
        //打开连接
        ssh.connect();
        //指定语句对象，参数为可变参数
        CustomTask execCommand = new ExecCommand("netstat -tun | grep \":50070\" && netstat -tun | grep \":50070\"|wc -l");

        Result result = ssh.exec(execCommand);
        out(result);
        //关闭连接
        ssh.disconnect();
    }

    @Before
    public void init() {
        //连接linux配置
        ConnBean connBean = new ConnBean("47.105.158.112", "root", "zd521707@");
//        ConnBean connBean = new ConnBean("server02", "root", "Zd521707@");
        SSHExec.setOption(IOptionName.SSH_PORT_NUMBER, 22222);
//        SSHExec.setOption(IOptionName.TIMEOUT, 36000l);

        //SSH连接对象
        ssh = SSHExec.getInstance(connBean);
        //打开连接
        ssh.connect();
    }

    @After
    public void close() {
        ssh.disconnect();
    }

    @Test
    public void uplod() throws Exception {
        //上传文件夹下全部文件到远程主机
//        ssh.uploadAllDataToServer("C:\\学习\\accesslog.dat", "/export/data");
        ssh.uploadSingleDataToServer("C:\\学习\\accesslog.dat", "/export/data");//TODO 上传单个文件
    }

    @Test
    public void execShell() throws Exception {
        //其中 workingDir 代表执行前先切换到路径，shellPath 代表脚本执行路径，args 代表参数列表。
//public ExecShellScript(String workingDir, String shellPath, String args)
//public ExecShellScript(String shellPath, String args)
//public ExecShellScript(String shellPath)
        CustomTask ct1 = new ExecShellScript("/home/tsadmin", "./sshxcute_test.sh", "hello world");
        ssh.exec(ct1);
    }

    @Test
    public void execCommand() throws Exception {
        //執行多個命令
//        CustomTask sampleTask = new ExecCommand("echo 123", "echo 456", "echo 789");
        CustomTask sampleTask = new ExecCommand("/export/servers/hadoop-2.7.5/bin/hadoop dfs -ls /");
        CustomTask task1 = new ExecCommand("source /etc/profile");
        CustomTask task2 = new ExecCommand("hdfs dfs -ls /");
        Result result1 = ssh.exec(task1);
        Result result2 = ssh.exec(task2);
        out(result2);
    }

    @Test
    public void execCommand2() throws Exception {
        //執行多個命令
//        CustomTask sampleTask = new ExecCommand("echo 123", "echo 456", "echo 789");
        CustomTask sampleTask = new ExecCommand("cd /export/servers/");
        CustomTask task1 = new ExecCommand("scp -r -P 22222 kafka_2.11-0.10.0.0/ server01:$PWD");
        CustomTask task2 = new ExecCommand("scp -r -P 22222 kafka_2.11-0.10.0.0/ server03:$PWD");
        Result exec = ssh.exec(sampleTask);
        Result result1 = ssh.exec(task1);
        Result result2 = ssh.exec(task2);
        out(result2);
    }

    @Test
    public void reportRun() throws Exception {
        //執行多個命令
//        CustomTask sampleTask = new ExecCommand("echo 123", "echo 456", "echo 789");
//        ssh.uploadSingleDataToServer("C:/study/javaStudy/bigData/java-common/flink_pyg/report/target/report.jar", "/export/servers");
        CustomTask task2 = new ExecCommand(
                "cd /export/servers/",
                "source /etc/profile",
                "nohup java -jar report.jar >>report.log 2>&1 &");

        Result result2 = ssh.exec(task2);
        out(result2);
    }

    public static void out(Result result) {
//        if (result.isSuccess) {
//            System.out.println("Return code: " + result.rc);
//            System.out.println(result.sysout);
//        } else {
//            System.out.println("Return code: " + result.rc);
//            System.out.println("error message: " + result.error_msg);
//        }
    }

}
