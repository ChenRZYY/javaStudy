package com.sdrfengmi.study._002_guava_lang3;

import java.math.BigDecimal;
public class NumberUtils {

    /**
     * 将字符串类型浮点数转为double类型，如果是不合法的浮点数则返回0
     * @param str
     * @return
     */
    public static double toDouble(String str) {
        return toDouble(str, 0.0D);
    }

    /**
     * 将字符串类型浮点数转为double类型，如果是不合法的浮点数则返回指定的默认值
     * @param str
     * @return
     */
    public static double toDouble(String str, double defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException var4) {
                return defaultValue;
            }
        }
    }

    /**
     * 将浮点数保留2位小数（四舍五入），返回字符串
     * @param value
     * @return
     */
    public static String scaleFormat(double value){
        return BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    /**
     * 将浮点数保留指定长度的小数（四舍五入），返回字符串
     * @param value
     * @param scale
     * @return
     */
    public static String scaleFormat(double value, int scale){
        return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString();
    }
    
}
