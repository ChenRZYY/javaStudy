package com.sdrfengmi.study._002_guava_lang3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateFormat {

    private static String ERR_INTERNAL_ERROR = "转换异常";

    private DateFormat() {

    }

    /**
     * 日期格式转换： YYYYMMDD格式转为YYYY-MM-DD
     *
     * @param dateStr
     * @return
     */
    public static String transform(String dateStr) throws RuntimeException {
        if (StringUtils.isEmpty(dateStr)) {
            return dateStr;
        }
        DateTimeFormatter fromDateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter toDateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        try {
            LocalDate fromDate = LocalDate.parse(dateStr, fromDateTimeFormatter);
            return fromDate.format(toDateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.error("input date is not a legal hsdate.", e);
            throw new RuntimeException(ERR_INTERNAL_ERROR);
        }
    }

    /**
     * 日期格式转换
     *
     * @param dateStr   时间字符串
     * @param orgFormat 原时间格式
     * @param dstFormat 需要转换的目标格式
     * @return
     */
    public static String transform(String dateStr, String orgFormat, String dstFormat) throws RuntimeException {
        SimpleDateFormat orgFmt = new SimpleDateFormat(orgFormat);
        SimpleDateFormat dstFmt = new SimpleDateFormat(dstFormat);
        try {
            Date date = orgFmt.parse(dateStr);
            return dstFmt.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(ERR_INTERNAL_ERROR);
        }
    }

    /**
     * 日期格式转换(反转)： YYYY-MM-DD格式转为YYYYMMDD
     *
     * @param dateStr
     * @return
     */
    public static String inverse(String dateStr) throws RuntimeException {
        DateTimeFormatter fromDateTimeFormatter =
                DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter toDateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMdd");
        try {
            LocalDate fromDate = LocalDate.parse(dateStr, fromDateTimeFormatter);
            return fromDate.format(toDateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.error("input date is not a legal hsdate.", e);
            throw new RuntimeException(ERR_INTERNAL_ERROR);
        }
    }

    /**
     * 将本地机器时间转换为指定格式字符串
     *
     * @param format 需要返回的时间格式， 如“yyyy-MM-dd”、"yyyyMMdd"、"yyyy-MM-dd HH:mm:ss"
     * @return 时间字符串
     */
    public static String getLocalTime(String format) {
        return getTimeString(new Date(), format);
    }

    /**
     * 将日期时间转换为指定格式字符串
     *
     * @param dateTime 日期时间
     * @param format   需要返回的时间格式， 如“yyyy-MM-dd”、"yyyyMMdd"、"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getTimeString(Date dateTime, String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return fmt.format(dateTime);
    }

    /**
     * 获取本地机器日期
     *
     * @return int型，如：20190410， 方便比较大小
     */
    public static int getLocalDate() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return Integer.parseInt(fmt.format(date));
    }

    /**
     * 获取本地机器时间
     *
     * @return int型，如：201536， 方便比较大小
     */
    public static int getLocalTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("HHmmss");
        Date date = new Date();
        return Integer.parseInt(fmt.format(date));
    }

    /**
     * 字符串转日期
     *
     * @param dateTime 日期字符串
     * @param format   字符串的格式
     * @return
     * @throws RuntimeException
     */
    public static Date strToDate(String dateTime, String format) throws RuntimeException {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        try {
            return fmt.parse(dateTime);
        } catch (ParseException e) {
            throw new RuntimeException(ERR_INTERNAL_ERROR);
        }
    }

}
