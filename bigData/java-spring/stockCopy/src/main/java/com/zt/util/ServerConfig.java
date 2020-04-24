package com.zt.util;

/**
 * @Author Haishi
 * @create 2020/4/24 09:31
 */
public class ServerConfig {

    /**
     * 外部配置文件地址 -test的为测试时的配置文件
     */
    private final static String OUTPATH_ZZTSERVER_TEST = "stockCopy/stock-conf/zztserver-test.properties";
    private final static String OUTPATH_SERVER_TEST = "stockCopy/stock-conf/site-test.properties";

//    public final static String OUTPATH_ZZTSERVER = "stockCopy/src/main/resources/zztserver.properties";
//    public final static String OUTPATH_SERVER = "stockCopy/src/main/resources/server.properties";

    private final static String OUTPATH_ZZTSERVER_PROD = "stockCopy/stock-conf/zztserver-prod.properties";
    private final static String OUTPATH_SERVER_PROD = "stockCopy/stock-conf/site-prod.properties";

    private static String PATH_ZZTSERVER = "";
    private static String PATH_SERVER = "";

    /**
     * 以前应用 内部配置文件地址(内外部配置文件内容一样)
     */
    @Deprecated
    public final static String INPATH_ZZTSERVER = "/zztserver.properties";
    @Deprecated
    public final static String INPATH_SERVER = "/server.properties";


    public final static String SipChinaMobile = "SipChinaMobile";
    public final static String SipChinaUnicom = "SipChinaUnicom";
    public final static String SipChinaTelecom = "SipChinaTelecom";
    public final static String server_push = "server.push";
    public final static String server_push2 = "server.push2";
    public final static String stock_trade_url = "stock.trade_url";
    public final static String stock_trade_port = "stock.trade_port";
    public final static String stock_information_url = "stock.information_url";
    public final static String stock_information_port = "stock.information_port";
    public final static String stock_quotes_url = "stock.quotes_url";
    public final static String stock_quotes_port = "stock.quotes_port";

    /**
     * 父类类型type 是那个服务
     */
    public final static String quotes = "quotes"; //行情
    public final static String trade = "trade"; //交易
    public final static String information = "information"; //咨询

    //环境信息
    public final static String TEST = "test";
    public final static String PROD = "prod";

    //线程数量
    public final static String server_threadSize = "server.threadSize";


    public static void intPath(String flag) {
        if (PROD.equals(flag)) {
            PATH_ZZTSERVER = OUTPATH_ZZTSERVER_PROD;
            PATH_SERVER = OUTPATH_SERVER_PROD;
        } else {
            PATH_ZZTSERVER = OUTPATH_ZZTSERVER_TEST;
            PATH_SERVER = OUTPATH_SERVER_TEST;
        }
    }

    public static String getZZTServerPath() {
        return PATH_ZZTSERVER;
    }

    public static String getServerPath() {
        return PATH_SERVER;
    }


}
