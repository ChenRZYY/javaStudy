//package com.sdrfengmi.springboot._002_resource;
//
//import java.io.*;
//import java.util.Properties;
//
///**
// * @Author Haishi
// * @create 2020/6/2 14:53
// */
//public class Config {
//    /*
//     * 读取属性配置文件返回的是Properties对象
//     *
//     * @param propertiesFileName
//     * @return
//     */
//    public static Properties readPropertiesFile(String proFileName) {
//        Properties properties = new Properties();
//        InputStream inCfg = null;
//        try {
//            inCfg = new BufferedInputStream(new FileInputStream(getConfigPath(proFileName)));
//            properties.load(inCfg);
//        } catch (Exception e) {
//            LocalFilewrite.localFileWirte("读取属性配置文件失败！位置PropertiesManager类readPropertiesFile(String proFileName)方法."
//                    + e.toString());
//        }
//        return properties;
//    }
//
//    private static String getConfigPath(String fileName)
//    {
//        String jarPath = PropertiesManager.class.getProtectionDomain().getCodeSource().getLocation().getFile();
//        try
//        {
//            jarPath = java.net.URLDecoder.decode(jarPath, "UTF-8");
//        }
//        catch (UnsupportedEncodingException e)
//        {
//            LocalFilewrite.localFileWirte(e.toString());
//        }
//        String filePath = (new File(jarPath)).getParentFile().getParentFile().getAbsolutePath() + "/" + "config" + "/" + fileName;
//
//        return filePath;
//    }
//
//}
