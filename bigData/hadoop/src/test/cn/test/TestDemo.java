package cn.test;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class TestDemo {

    @Test
    public void demo1()throws  Exception{
        //第一步：注册hdfs 的url

        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());

        //获取文件输入流
        InputStream inputStream  = new URL("hdfs://server02:8020/globalParameter/globalParameter.txt").openStream();
        //获取文件输出流
        FileOutputStream outputStream = new FileOutputStream(new File("C:\\学习\\test.txt"));

        //实现文件的拷贝
        IOUtils.copy(inputStream, outputStream);

        //关闭流
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
    }
}
