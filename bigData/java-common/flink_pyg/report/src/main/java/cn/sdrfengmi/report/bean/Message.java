package cn.sdrfengmi.report.bean;

import lombok.Data;

@Data
public class Message {
    // 消息次数
    private int count;

    // 消息的时间戳
    private long timeStamp;

    // 消息体
    private String message;

    @Override
    public String toString() {
        return "Message{" +
                "count=" + count +
                ", timeStamp=" + timeStamp +
                ", message='" + message + '\'' +
                '}';
    }
}
