package com.zt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesUtil {

//    private static Properties properties;

    public final static String UTF_8 = "utf-8";

    /**
     * 读取resource下的配置文件
     *
     * @date 2019年3月26日
     * @author 陈振东
     * zztServer 加载的properties地址
     */
    public static Properties init(String zztServer) {
        Properties properties = new Properties();
        InputStream in = PropertiesUtil.class.getResourceAsStream(zztServer);
        try {
            if (in == null) {
                throw new FileNotFoundException();
            }
            properties.load(in);
            return properties;
        } catch (Exception e) {
            log.error("", e);
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
    }

    public static Properties outInit(String outPathStr) {
        Properties properties = new Properties();

        File parentFile = new File("").getAbsoluteFile();
        //判断当前系统是windows/还是linux 不同的系统获取路径不一样 跑本地时放开
//        Properties prop = System.getProperties();
//        String os = prop.getProperty("os.name");
//        if (os != null && os.toLowerCase().indexOf("windows") > -1) {
//            parentFile = new File("").getAbsoluteFile().getParentFile();
//         } 
        File file = new File(parentFile.getPath(), new File(outPathStr).getPath());
        FileInputStream fis = null;
        InputStreamReader reader = null;
        try {
            fis = new FileInputStream(file);
            reader = new InputStreamReader(fis, UTF_8);
            properties.load(reader);
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        return properties;
    }

    public static String getConfig( String key) {
        //TODO 用于加载内外部文件
        Properties properties = outInit(ServerConfig.getZZTServerPath());
        return properties.getProperty(key);
    }

    /**
     * 获取所有属性
     */
    public static Properties readAll(String fileName) {
        InputStreamReader in = null;
        Properties props = new Properties();
        try {
            in = new InputStreamReader(PropertiesUtil.class.getResourceAsStream(fileName), UTF_8);
            props.load(in);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        return props;
    }
}
