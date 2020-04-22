package com.sdrfengmi.study._002_guava_lang3;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @Author 陈振东
 * @create 2020/4/21 14:37
 */
public class DateUtilTest {

    // 当前时间
    Date date = new Date();
    // 时间输出
    Consumer<Date> consumer = object -> {
        String format = DateFormatUtils.format(object, "yyyy-MM-dd HH:mm:ss");
        System.out.println(format);
    };

    @Test
    public void format() {

        String format = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        System.err.println(format);

        FastDateFormat dateInstance = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        String country = dateInstance.getLocale().getCountry();
        System.err.println(dateInstance.getLocale().getCountry());
        System.err.println(dateInstance.format(new Date()));
    }

    @Test
    public void addDate() {
        // 当前日期加1年
        DateUtils.addYears(date, 1);
        // 当前日期加1个月
        DateUtils.addMonths(date, 1);
        // 当前日期加1天
        DateUtils.addDays(date, 1);
        // 当前日期加1个星期
        DateUtils.addWeeks(date, 1);
        // 当前日期加1小时
        DateUtils.addHours(date, 1);
        // 当前日期加1分钟
        DateUtils.addMinutes(date, 1);
        // 当前日期加1秒钟
        DateUtils.addSeconds(date, 1);
        // 当前日期加1毫秒
        DateUtils.addMilliseconds(date, 1);
    }

    @Test
    public void split() {
        Date ceilYear = DateUtils.ceiling(date, Calendar.YEAR);
        Date ceilMonth = DateUtils.ceiling(date, Calendar.MONTH);
        Date ceilDay = DateUtils.ceiling(date, Calendar.DAY_OF_MONTH);
        Date ceilHour = DateUtils.ceiling(date, Calendar.HOUR);
        Date ceilMinute = DateUtils.ceiling(date, Calendar.MINUTE);
        Date ceilSecode = DateUtils.ceiling(date, Calendar.SECOND);

        consumer.accept(date);//2018-11-23 11:32:12
        consumer.accept(ceilYear);//2019-01-01 00:00:00，截取到年，年向上取整（即+1）
        consumer.accept(ceilMonth);//2018-12-01 00:00:00，截取到月，月向上取整（即+1）；如果是12月的话，月进1变成01，年进1
        consumer.accept(ceilDay);//2018-11-24 00:00:00，截取到日，日向上取整（即+1）；如果是30（或28/31）的话，日进1变成01，月进1
        consumer.accept(ceilHour);//2018-11-23 12:00:00，截取到时，时向上取整（即+1）；如果是23点的话，时进1变成00，日进1
        consumer.accept(ceilMinute);//2018-11-23 11:33:00，截取到分，分向上取整（即+1）；如果是59分的话，分进1变成00，时进1
        consumer.accept(ceilSecode);//2018-11-23 11:32:13，截取到分，分向上取整（即+1）
    }
}
