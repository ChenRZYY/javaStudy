package cn.sdrfengmi.spark.project._01_hadoop_Offline.WebLogClean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

public class WebLogParser {

    public static SimpleDateFormat df1 = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
    public static SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static WebLogBean parser(String line) {
        String[] datas = line.split(" ");
        if (datas.length > 11) {
            WebLogBean webLogBean = new WebLogBean();
            webLogBean.setRemote_addr(datas[0]);
            webLogBean.setRemote_user(datas[1]);
            String time_local = formatDate(datas[3].substring(1));
            if(null==time_local || "".equals(time_local)) time_local="-invalid_time-";
//            time_local = (null == time_local || "".equals(time_local)) ? time_local : "-invalid_time-";
            webLogBean.setTime_local(time_local);
            webLogBean.setRequest(datas[6]);
//			System.out.println(arr[6]+"-----------------------------");
            webLogBean.setStatus(datas[8]);
            webLogBean.setBody_bytes_sent(datas[9]);
            webLogBean.setHttp_referer(datas[10]);

            //如果useragent元素较多，拼接useragent
            if (datas.length > 12) {
                StringBuilder sb = new StringBuilder();
                for (int i = 11; i < datas.length; i++) {
                    sb.append(datas[i]);
                }
                webLogBean.setHttp_user_agent(sb.toString());
            } else {
                webLogBean.setHttp_user_agent(datas[11]);
            }

            if (Integer.parseInt(webLogBean.getStatus()) >= 400) {// 大于400，HTTP错误
                webLogBean.setValid(false);
            }

            if ("-invalid_time-".equals(webLogBean.getTime_local())) {
                webLogBean.setValid(false);
            }
            return webLogBean;
        }
        return null;
    }

    public static void filtStaticResource(WebLogBean bean, Set<String> pages) {
        if (!pages.contains(bean.getRequest())) {
            bean.setValid(false);
        }
    }

    //格式化时间方法
    public static String formatDate(String time_local) {
        try {
            return df2.format(df1.parse(time_local));
        } catch (ParseException e) {
            return null;
        }

    }
}
