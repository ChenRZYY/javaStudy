package cn.sdrfengmi.spark.project._01_hadoop_Offline.WebLogClean;

import lombok.Data;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 对接外部数据的层，表结构定义最好跟外部数据源保持一致
 * 术语： 贴源表
 */
@Data
public class WebLogBean implements Writable {

    private boolean valid = true;// 判断数据是否合法
    private String remote_addr;// 记录客户端的ip地址
    private String remote_user;// 记录客户端用户名称,忽略属性"-"
    private String time_local;// 记录访问时间与时区
    private String request;// 记录请求的url与http协议
    private String status;// 记录请求状态；成功是200
    private String body_bytes_sent;// 记录发送给客户端文件主体内容大小
    private String http_referer;// 用来记录从那个页面链接访问过来的
    private String http_user_agent;// 记录客户浏览器的相关信息

    public void setBean(boolean valid, String remote_addr, String remote_user, String time_local, String request, String status, String body_bytes_sent, String http_referer, String http_user_agent) {
        this.valid = valid;
        this.remote_addr = remote_addr;
        this.remote_user = remote_user;
        this.time_local = time_local;
        this.request = request;
        this.status = status;
        this.body_bytes_sent = body_bytes_sent;
        this.http_referer = http_referer;
        this.http_user_agent = http_user_agent;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(this.valid);
        out.writeUTF(this.remote_addr);
        out.writeUTF(this.remote_user);
        out.writeUTF(this.time_local);
        out.writeUTF(this.request);
        out.writeUTF(this.status);
        out.writeUTF(this.body_bytes_sent);
        out.writeUTF(this.http_referer);
        out.writeUTF(this.http_user_agent);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.valid = in.readBoolean();
        this.remote_addr = in.readUTF();
        this.remote_user = in.readUTF();
        this.time_local = in.readUTF();
        this.request = in.readUTF();
        this.status = in.readUTF();
        this.body_bytes_sent = in.readUTF();
        this.http_referer = in.readUTF();
        this.http_user_agent = in.readUTF();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.valid);
        sb.append("\001").append(this.getRemote_addr());
        sb.append("\001").append(this.getRemote_user());
        sb.append("\001").append(this.getTime_local());
        sb.append("\001").append(this.getRequest());
        sb.append("\001").append(this.getStatus());
        sb.append("\001").append(this.getBody_bytes_sent());
        sb.append("\001").append(this.getHttp_referer());
        sb.append("\001").append(this.getHttp_user_agent());
        return sb.toString();
    }
}
