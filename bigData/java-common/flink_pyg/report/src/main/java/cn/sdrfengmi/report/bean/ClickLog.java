package cn.sdrfengmi.report.bean;

import lombok.Data;

/**
 * 点击流日志
 */
@Data
public class ClickLog {
    //频道ID
    private long channelID;
    //产品的类别ID
    private long categoryID;
    //产品ID
    private long produceID;
    //用户的ID
    private long userID;

    //国家
    private String country;
    //省份
    private String province;
    //城市
    private String city;


    //网络方式
    private String network;
    //来源方式
    private String source;

    //浏览器类型
    private String browserType;

    //进入网站时间
    private Long entryTime;

    //离开网站时间
    private long leaveTime;

    @Override
    public String toString() {
        return "ClickLog{" +
                "channelID=" + channelID +
                ", categoryID=" + categoryID +
                ", produceID=" + produceID +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", network='" + network + '\'' +
                ", source='" + source + '\'' +
                ", browserType='" + browserType + '\'' +
                ", entryTime=" + entryTime +
                ", leaveTime=" + leaveTime +
                ", userID=" + userID +
                '}';
    }
}
