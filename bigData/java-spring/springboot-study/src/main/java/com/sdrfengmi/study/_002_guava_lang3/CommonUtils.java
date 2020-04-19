package com.sdrfengmi.study._002_guava_lang3;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.time.StopWatch;

import com.google.common.collect.Maps;

/**
 * @author 陈振东
 */
public class CommonUtils {

    /**
     * StringUtils
     * 常用工具类, 截取字符串
     */
    public static void stringUtils1_1() {
        System.out.println(StringUtils.isBlank("   "));// true----可以验证null, ""," "等
        System.out.println(StringUtils.isBlank("null"));// false
        System.out.println(StringUtils.isAllLowerCase("null"));// true
        System.out.println(StringUtils.isAllUpperCase("XXXXXX"));// true
        System.out.println(StringUtils.isEmpty(" "));// f---为null或者""返回true
        System.out.println(StringUtils.defaultIfEmpty(null, "default"));// 第二个参数是第一个为null或者""的时候的取值
        System.out.println(StringUtils.defaultIfBlank("    ", "default"));//// 第二个参数是第一个为null或者""或者"   "的时候的取值

        System.out.println(StringUtils.isNoneBlank("    ", "default", ""));//flase
        System.out.println(StringUtils.isNoneBlank(null));//flase
        System.out.println(StringUtils.isNoneBlank("foo", "bar"));//true 可以支持一个或者多个  String数组

        String str = "tttt";

        StringUtils.countMatches(str, ":");//        补充:StringUtils可以获取指定字符出现的次数
        StringUtils.ordinalIndexOf(str, ":", 2); //         补充:StringUtils可以第N次某字符串出现的位置
        String[] substringBetweens = StringUtils.substringsBetween(str, ":", ":"); //        补充:StringUtils可以获取指定字符串之间的字符串，并自动截取为字符串数组
        StringUtils.substringBetween("陈", "/"); //        补充:StringUtils可以获取指定字符串之间的字符串(只取满足条件的前两个)

        // 左右截取         补充:Stringutils可以左截取和右截取
        System.out.println(StringUtils.left("abc", 2));
        System.out.println(StringUtils.right("abc", 2));

        String string = "123_45_43_ss";
        System.out.println(StringUtils.isAllLowerCase(string));// 判断全部小写
        System.out.println(StringUtils.isAllUpperCase(string));// 判断全部大写
        System.out.println(StringUtils.substringAfter(string, "123"));// 截取123之后的
        System.out.println(StringUtils.substringBefore(string, "45"));// 截取45之前的
        System.out.println(StringUtils.substringBefore(string, "_"));// 截取第一个_之前的
        System.out.println(StringUtils.substringBeforeLast(string, "_"));// 截取最后一个_之前的
        System.out.println(StringUtils.substringAfter(string, "_"));// 截取第一个_之后的
        System.out.println(StringUtils.substringAfterLast(string, "_"));// 截取最后一个_之后的
        System.out.println(StringUtils.substringBetween("1234565432123456", "2", "6"));// 截取两个之间的(都找的是第一个)

        List<String> list = Arrays.asList("张三", "李四", "王五");
        String list2str = StringUtils.join(list, ",");
        System.out.println(list2str); //张三,李四,王五
        int[] numbers =
                {1, 3, 5};
    }

    /**
     * org.apache.commons.lang.StringUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void stringUtils1_2() {
        //        isBlank：字符串是否为空 (trim后判断)
        //        isEmpty：字符串是否为空 (不trim并判断)
        //        equals：字符串是否相等
        //        join：合并数组为单一字符串，可传分隔符
        //        split：分割字符串
        //        EMPTY：返回空字符串
        //        trimToNull：trim后为空字符串则转换为null
        //        replace：替换字符串

    }

    /**
     * org.apache.commons.lang3.StringUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void stringUtils1_3() {
        //        isBlank：字符串是否为空 (trim后判断)
        //        isEmpty：字符串是否为空 (不trim并判断)
        //        equals：字符串是否相等
        //        join：合并数组为单一字符串，可传分隔符
        //        split：分割字符串
        //        EMPTY：返回空字符串
        //        replace：替换字符串
        //        capitalize：首字符大写        
    }

    /**
     * org.springframework.util.StringUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void stringUtils1_4() {
        //        hasText：检查字符串中是否包含文本
        //        hasLength：检测字符串是否长度大于0
        //        isEmpty：检测字符串是否为空（若传入为对象，则判断对象是否为null）
        //        commaDelimitedStringToArray：逗号分隔的String转换为数组
        //        collectionToDelimitedString：把集合转为CSV格式字符串
        //        replace 替换字符串
        //        delimitedListToStringArray：相当于split
        //        uncapitalize：首字母小写
        //        collectionToDelimitedCommaString：把集合转为CSV格式字符串
        //        tokenizeToStringArray：和split基本一样，但能自动去掉空白的单词
    }

    /**
     * StringEscapeUtils
     */
    //    public static  void test2(){
    //        //1.防止sql注入------原理是将'替换为''
    //        System.out.println(org.apache.commons.lang.StringEscapeUtils.escapeSql("sss"));
    //        //2.转义/反转义html
    //        System.out.println( org.apache.commons.lang.StringEscapeUtils.escapeHtml("<a>dddd</a>"));   //&lt;a&gt;dddd&lt;/a&gt;
    //        System.out.println(org.apache.commons.lang.StringEscapeUtils.unescapeHtml("&lt;a&gt;dddd&lt;/a&gt;"));  //<a>dddd</a>
    //        //3.转义/反转义JS
    //        System.out.println(org.apache.commons.lang.StringEscapeUtils.escapeJavaScript("<script>alert('1111')</script>"));   
    //        //4.把字符串转为unicode编码
    //        System.out.println(org.apache.commons.lang.StringEscapeUtils.escapeJava("中国"));   
    //        System.out.println(org.apache.commons.lang.StringEscapeUtils.unescapeJava("\u4E2D\u56FD"));  
    //        //5.转义JSON
    //        System.out.println(org.apache.commons.lang3.StringEscapeUtils.escapeJson("{name:'qlq'}"));   
    //    }

    /**
     * NumberUtils
     */
    public static void numberUtils3() {
        System.out.println(NumberUtils.isNumber("231232.8"));//true---判断是否是数字
        System.out.println(NumberUtils.isDigits("2312332.5"));//false，判断是否是整数
        System.out.println(NumberUtils.toDouble(null));//如果传的值不正确返回一个默认值，字符串转double，传的不正确会返回默认值
        System.out.println(NumberUtils.createBigDecimal("333333"));//字符串转bigdecimal
    }

    /**
     * BooleanUtils
     */
    public static void booleanUtils4() {
        System.out.println(BooleanUtils.isFalse(true));//false
        System.out.println(BooleanUtils.toBoolean("yes"));//true
        System.out.println(BooleanUtils.toBooleanObject(0));//false
        System.out.println(BooleanUtils.toStringYesNo(false));//no
        System.out.println(BooleanUtils.toBooleanObject("ok", "ok", "error", "null"));//true-----第一个参数是需要验证的字符串，第二个是返回true的值，第三个是返回false的值，第四个是返回null的值
    }

    /**
     * SystemUtils
     */
    public static void systemUtils5() {
        System.out.println(SystemUtils.getJavaHome());
        System.out.println(SystemUtils.getJavaIoTmpDir());
        System.out.println(SystemUtils.getUserDir());
        System.out.println(SystemUtils.getUserHome());
        System.out.println(SystemUtils.JAVA_VERSION);
        System.out.println(SystemUtils.OS_NAME);
        System.out.println(SystemUtils.USER_TIMEZONE);
    }

    /**
     * DateUtils和DateFormatUtils可以实现字符串转date与date转字符串,date比较先后问题
     * DateUtils也可以判断是否是同一天等操作。
     * 自己开发 不能用成员变量dateFormat  可以用 谷歌的 Fast
     */
    public static void dateFormatUtils6() {
        // DateFormatUtils----date转字符串
        Date date = new Date();
        System.out.println(DateFormatUtils.format(date, "yyyy-MM-dd hh:mm:ss"));// 小写的是12小时制
        System.out.println(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));// 大写的HH是24小时制

        // DateUtils ---加减指定的天数(也可以加减秒、小时等操作)
        Date addDays = DateUtils.addDays(date, 2);
        System.out.println(DateFormatUtils.format(addDays, "yyyy-MM-dd HH:mm:ss"));
        Date addDays2 = DateUtils.addDays(date, -2);
        System.out.println(DateFormatUtils.format(addDays2, "yyyy-MM-dd HH:mm:ss"));

        // 原生日期判断日期先后顺序
        System.out.println(addDays2.after(addDays));
        System.out.println(addDays2.before(addDays));

        // DateUtils---字符串转date
        String strDate = "2018-11-01 19:23:44";
        try {
            Date parseDateStrictly = DateUtils.parseDateStrictly(strDate, "yyyy-MM-dd HH:mm:ss");
            Date parseDate = DateUtils.parseDate(strDate, "yyyy-MM-dd HH:mm:ss");
            System.out.println(parseDateStrictly);
            System.out.println(parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //成员变量 没有并发问题
        FastDateFormat instance = FastDateFormat.getInstance();
        String format = instance.format(new Date());
    }

    /**
     * 7.StopWatch提供秒表的计时,暂停等功能
     *
     * @date 2019年11月12日
     * @author 陈振东
     */
    public static void stopWatch7() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        System.out.println(stopWatch.getStartTime());// 获取开始时间
        System.out.println(stopWatch.getTime());// 获取总的执行时间--单位是毫秒
    }

    /**
     * @date 2019年11月12日
     * @author 陈振东
     * 8.以Range结尾的类主要提供一些范围的操作,包括判断某些字符,数字等是否在这个范围以内
     */
    public static void range8() {
        //        IntRange intRange = new IntRange(1, 5);
        //        System.out.println(intRange.getMaximumInteger());
        //        System.out.println(intRange.getMinimumInteger());
        //        System.out.println(intRange.containsInteger(6));
        //        System.out.println(intRange.containsDouble(3));
    }

    /**
     * @date 2019年11月12日
     * @author 陈振东
     * 9.ArrayUtils操作数组，功能强大，可以合并，判断是否包含等操作
     * org.apache.commons.lang.ArrayUtils
     */
    public static void arrayUtils9_1() {
        int array[] =
                {1, 5, 5, 7};
        System.out.println(array);

        // 增加元素
        array = ArrayUtils.add(array, 9);
        System.out.println(ArrayUtils.toString(array));

        // 删除元素
        array = ArrayUtils.remove(array, 3);
        System.out.println(ArrayUtils.toString(array));

        // 反转数组
        ArrayUtils.reverse(array);
        System.out.println(ArrayUtils.toString(array));

        // 查询数组索引
        System.out.println(ArrayUtils.indexOf(array, 5));

        // 判断数组中是否包含指定值
        System.out.println(ArrayUtils.contains(array, 5));

        // 合并数组
        array = ArrayUtils.addAll(array, new int[]
                {1, 5, 6});
        System.out.println(ArrayUtils.toString(array));

        //补充:ArrayUtils可以将包装类型的数组转变为基本类型的数组。
        Integer integer[] = new Integer[]
                {0, 1, 2};
        System.out.println(integer.getClass());

        int[] primitive = ArrayUtils.toPrimitive(integer);
        System.out.println(primitive.getClass());

        array = ArrayUtils.removeElement(array, 5); // {1,6} 删除元素
        //        结果:
        //        [I@3cf5b814
        //        {1,5,5,7,9}
        //        {1,5,5,9}
        //        {9,5,5,1}
        //        1
        //        true
        //        {9,5,5,1,1,5,6}

    }

    /**
     * @date 2019年11月13日
     * @author 陈振东
     * org.apache.commons.lang3.ArrayUtils
     */
    public static void arrayUtils9_2() {
        //    contains：是否包含某字符串
        //    addAll：添加整个数组
        //    clone：克隆一个数组
        //    isEmpty：是否空数组
        //    add：向数组添加元素
        //    subarray：截取数组
        //    indexOf：查找某个元素的下标
        //    isEquals：比较数组是否相等
        //    toObject：基础类型数据数组转换为对应的Object数组
    }

    /**
     * @date 2019年11月12日
     * @author 陈振东
     * 10.  RandomStringUtils可用于生成随机数和随机字符串
     */
    public static void randomStringUtils10() {
        // 第一个参数表示生成位数，第二个表示是否生成字母，第三个表示是否生成数字
        System.out.println(RandomStringUtils.random(15, true, false));

        // 长度、起始值、结束值、是否使用字母、是否生成数字
        System.out.println(RandomStringUtils.random(15, 5, 129, true, false));
        System.out.println(RandomStringUtils.random(22));

        // 从指定字符串随机生成
        System.out.println(RandomStringUtils.random(15, "abcdefgABCDEFG123456789"));

        // 从字母中抽取
        System.out.println(RandomStringUtils.randomAlphabetic(15));

        // 从数字抽取
        System.out.println(RandomStringUtils.randomNumeric(15));

        // ASCII between 32 and 126 =内部调用(random(count, 32, 127, false,false);)
        System.out.println(RandomStringUtils.randomAscii(15));
    }

    /**
     * @date 2019年11月12日
     * @author 陈振东
     * 11 CollectionUtils工具类用于操作集合,  isEmpty () 方法最有用   （commons-collections包中的类）
     * org.apache.commons.collections.CollectionUtils
     */
    public static void collectionUtils11_1() {
        List<String> list = Arrays.asList("str1", "str2");
        List<String> list1 = Arrays.asList("str1", "str21");

        // 判断是否有任何一个相同的元素
        System.out.println(CollectionUtils.containsAny(list, list1)); //true

        // 求并集(自动去重)
        List<String> list3 = (List<String>) CollectionUtils.union(list, list1); //[str2, str21, str1]
        System.out.println(list3);

        // 求交集(两个集合中都有的元素)
        Collection intersection = CollectionUtils.intersection(list, list1);
        System.out.println("intersection->" + intersection); //intersection->[str1]

        // 求差集(并集去掉交集，也就是list中有list1中没有，list1中有list中没有)
        Collection intersection1 = CollectionUtils.disjunction(list, list1);
        System.out.println("intersection1->" + intersection1); //intersection1->[str2, str21]

        // 获取一个同步的集合
        Collection synchronizedCollection = CollectionUtils.synchronizedCollection(list);

        // 验证集合是否为null或者集合的大小是否为0，同理有isNouEmpty方法
        List list4 = null;
        List list5 = new ArrayList<>();
        System.out.println(CollectionUtils.isEmpty(list4)); // true
        System.out.println(CollectionUtils.isEmpty(list5)); // true

        String s[] =
                {"1", "2"};
        CollectionUtils.addAll(list5, s);
        list5.add("3");
        System.out.println(list5); //[1, 2, 3]

    }

    /**
     * @date 2019年11月13日
     * @author 陈振东
     * org.apache.commons.collections.CollectionUtils
     */
    public static void collectionUtils11_2() {
        //        isEmpty：是否为空
        //        select：根据条件筛选集合元素
        //        transform：根据指定方法处理集合元素，类似List的map()
        //        filter：过滤元素，雷瑟List的filter()
        //        find：基本和select一样
        //        collect：和transform 差不多一样，但是返回新数组
        //        forAllDo：调用每个元素的指定方法
        //        isEqualCollection：判断两个集合是否一致
    }

    /**
     * @date 2019年11月13日
     * @author 陈振东
     * org.apache.commons.collections.MapUtils
     */
    public static void mapUtils13() {

        Map<Object, Object> newHashMap = Maps.newHashMap();
        Map map = null;
        Map map2 = new HashMap();
        Map map3 = new HashMap<>();
        map3.put("xxx", "xxx");
        // 检验为empty可以验证null和size为0的情况
        System.out.println(MapUtils.isEmpty(map)); //true
        System.out.println(MapUtils.isEmpty(map2));//true
        System.out.println(MapUtils.isEmpty(map3));//false

        String string = MapUtils.getString(map3, "eee");
        String string2 = MapUtils.getString(map3, "xxx");
        Integer integer = MapUtils.getInteger(map3, "xxx");
        System.out.println("string->" + string); //string->null   
        System.out.println("string2->" + string2); //string2->xxx    
        System.out.println("integer->" + integer); //integer->null  
        System.out.println(integer == null); //true

        //获取字符串,如果获取不到可以返回一个默认值
        String string3 = MapUtils.getString(map3, "eee", "没有值");
    }

    /**
     * 一. org.apache.commons.io.IOUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void iOUtils14() {
        //    closeQuietly：关闭一个IO流、socket、或者selector且不抛出异常，通常放在finally块
        //    toString：转换IO流、 Uri、 byte[]为String
        //    copy：IO流数据复制，从输入流写到输出流中，最大支持2GB
        //    toByteArray：从输入流、URI获取byte[]
        //    write：把字节. 字符等写入输出流
        //    toInputStream：把字符转换为输入流
        //    readLines：从输入流中读取多行数据，返回List<String>
        //    copyLarge：同copy，支持2GB以上数据的复制
        //    lineIterator：从输入流返回一个迭代器，根据参数要求读取的数据量，全部读取，如果数据不够，则失败
    }

    /**
     * org.apache.commons.io.FileUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void fileUtils15() {
        //        cleanDirectory：清空目录，但不删除目录
        //      contentEquals：比较两个文件的内容是否相同
        //      copyDirectory：将一个目录内容拷贝到另一个目录。可以通过FileFilter过滤需要拷贝的文件。
        //      copyFile：将一个文件拷贝到一个新的地址
        //      copyFileToDirectory：将一个文件拷贝到某个目录下
        //      copyInputStreamToFile：将一个输入流中的内容拷贝到某个文件
        //      deleteDirectory：删除目录
        //      deleteQuietly：删除文件
        //      listFiles：列出指定目录下的所有文件
        //      openInputSteam：打开指定文件的输入流
        //      readFileToString：将文件内容作为字符串返回
        //      readLines：将文件内容按行返回到一个字符串数组中
        //      size：返回文件或目录的大小
        //      write：将字符串内容直接写到文件中
        //      writeByteArrayToFile: 将字节数组内容写到文件中
        //      writeLines：将容器中的元素的toString方法返回的内容依次写入文件中
        //      writeStringToFile：将字符串内容写到文件中
        //    forceDelete：强制删除文件
    }

    /**
     * 四. org.apache.http.util.EntityUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void EntityUtils16() {
        //    toString：把Entity转换为字符串
        //    consume：确保Entity中的内容全部被消费。可以看到源码里又一次消费了Entity的内容，假如用户没有消费，那调用Entity时候将会把它消费掉
        //    toByteArray：把Entity转换为字节流
        //    consumeQuietly：和consume一样，但不抛异常
        //    getContentCharset：获取内容的编码
    }

    /**
     * @date 2019年11月13日
     * @author 陈振东
     * 六. org.apache.commons.io.FilenameUtils
     */
    public static void filenameUtils17() {
        //    getExtension：返回文件后缀名
        //    getBaseName：返回文件名，不包含后缀名
        //    getName：返回文件全名
        //    concat：按命令行风格组合文件路径(详见方法注释)
        //    removeExtension：删除后缀名
        //    normalize：使路径正常化
        //    wildcardMatch：匹配通配符
        //    seperatorToUnix：路径分隔符改成unix系统格式的，即/
        //    getFullPath：获取文件路径，不包括文件名
        //    isExtension：检查文件后缀名是不是传入参数(List<String>)中的一个
        //
    }

    //    九. org.apache.commons.lang.StringEscapeUtils
    //
    //    参考十五：
    //    org.apache.commons.lang3.StringEscapeUtils
    //
    //
    //    十. org.apache.http.client.utils.URLEncodedUtils
    //
    //    format：格式化参数，返回一个HTTP POST或者HTTP PUT可用application/x-www-form-urlencoded字符串
    //    parse：把String或者URI等转换为List<NameValuePair>
    //
    //     
    //

    /**
     * @date 2019年11月13日
     * @author 陈振东
     * org.apache.commons.codec.digest.DigestUtils
     */
    public static void digestUtils19() {
        //    md5Hex：MD5加密，返回32位字符串
        //    sha1Hex：SHA-1加密
        //    sha256Hex：SHA-256加密
        //    sha512Hex：SHA-512加密
        //    md5：MD5加密，返回16位字符串
    }

    /**
     * org.apache.commons.beanutils.PropertyUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void PropertyUtils20() {
        //    getProperty：获取对象属性值
        //    setProperty：设置对象属性值
        //    getPropertyDiscriptor：获取属性描述器
        //    isReadable：检查属性是否可访问
        //    copyProperties：复制属性值，从一个对象到另一个对象
        //    getPropertyDiscriptors：获取所有属性描述器
        //    isWriteable：检查属性是否可写
        //    getPropertyType：获取对象属性类型
    }

    /**
     * org.apache.commons.lang3.StringEscapeUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void StringEscapeUtils21() {
        //    unescapeHtml4：转义html
        //    escapeHtml4：反转义html
        //    escapeXml：转义xml
        //    unescapeXml：反转义xml
        //    escapeJava：转义unicode编码
        //    escapeEcmaScript：转义EcmaScript字符
        //    unescapeJava：反转义unicode编码
        //    escapeJson：转义json字符
        //    escapeXml10：转义Xml10
        //    这个现在已经废弃了，建议使用commons-text包里面的方法。
        //
    }

    /**
     * org.apache.commons.beanutils.BeanUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void BeanUtils22() {
        //    copyPeoperties：复制属性值，从一个对象到另一个对象
        //    getProperty：获取对象属性值
        //    setProperty：设置对象属性值
        //    populate：根据Map给属性复制
        //    copyPeoperty：复制单个值，从一个对象到另一个对象
        //    cloneBean：克隆bean实例
    }
    //
    //    　　现在你只要了解了以上16种最流行的工具类方法，你就不必要再自己写工具类了，不必重复造轮子。大部分工具类方法通过其名字就能明白其用途，如果不清楚的，可以看下别人是怎么用的，或者去网上查询其用法。
    //    　　另外，工具类，根据阿里开发手册，包名如果要使用util不能带s，工具类命名为 XxxUtils(参考spring命名)


    /**
     * org.apache.commons.beanutils.BeanUtils
     *
     * @date 2019年11月13日
     * @author 陈振东
     */
    public static void DecimalFormat23() {
        //    copyPeoperties：复制属性值，从一个对象到另一个对象
        //    getProperty：获取对象属性值
        //    setProperty：设置对象属性值
        //    populate：根据Map给属性复制
        //    copyPeoperty：复制单个值，从一个对象到另一个对象
        //    cloneBean：克隆bean实例
    }

}
