package cn.sdrfengmi._01_mapReduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class HdfsUtil {

    private static String serverUrl = "hdfs://server02:8020";
    private static String studyPath = "/study";

    public static String serverInputPath = "hdfs://server02:8020/study/";
    public static String serverOutputPath = "hdfs://server02:8020/study/";

    public static void deleteHDFSfile(String url) throws IOException, URISyntaxException {
        //获取FileSystem
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://server02:8020"), new Configuration());
        //判断目录是否存在
        Path path = new Path(url);
        boolean bl2 = fileSystem.exists(path);
        if (bl2) {
            //删除目标目录
            fileSystem.delete(path, true);
        }
        fileSystem.close();
    }

    public static void deleteHdfsInputSourceFile(Class clazz) throws IOException, URISyntaxException {
        String[] split = clazz.getName().split("\\.");
        String path = serverOutputPath + "hadoop"+split[split.length - 2] + "_input";
        deleteHDFSfile(path);
    }

    public static void deleteHdfsOutputSourceFile(Class clazz) throws IOException, URISyntaxException {
        String[] split = clazz.getName().split("\\.");
        String path = serverOutputPath + "hadoop" + split[split.length - 2] + "_output";
        deleteHDFSfile(path);
    }


    public static void uploadHdfsInputSourceFile(Class clazz) throws Exception {
        String[] split = clazz.getName().split("\\.");
        String fileName = split[split.length - 2];
        String url = URLDecoder.decode(clazz.getResource("").getPath() + "source.txt", "utf-8");
        url = url.replace("/target/classes", "/src/main/java");
        //1:获取FileSystem
        FileSystem fileSystem = FileSystem.get(new URI(serverUrl), new Configuration());
        //2:调用方法，实现上传
        fileSystem.copyFromLocalFile(new Path(url), new Path(studyPath + "/hadoop" + fileName + "_input/source.txt"));
//        fileSystem.rename(new Path(studyPath +fileName+ "/source.txt"), new Path(studyPath + fileName+"_input/" + fileName+".txt"));
        //3:关闭FileSystem
        fileSystem.close();
    }

//      public static  String getOutputStr(Class clazz) throws IOException, URISyntaxException {
//        String[] split = clazz.getName().split("\\.");
//        String path = serverOutputPath + split[split.length - 2]+"_output";
//        return path;
//    }

    public static Path getHdfsOutputPath(Class clazz) throws IOException, URISyntaxException {
        String[] split = clazz.getName().split("\\.");
        String path = serverOutputPath + "hadoop"+split[split.length - 2] + "_output";
        return new Path(path);
    }

    public static Path getHdfsInputputPath(Class clazz) throws IOException, URISyntaxException {
        String[] split = clazz.getName().split("\\.");
        String path = serverInputPath + "hadoop"+split[split.length - 2] + "_input";
        return new Path(path);
    }

    public static Path getLocalInputSourcePath(Class clazz) throws UnsupportedEncodingException {
        String url = URLDecoder.decode(clazz.getResource("").getPath() + "source.txt", "utf-8");
        url = url.replace("/target/classes", "/src/main/java");
        return new Path(url);
    }

    public static Path getLocalInputSourcePath(Class clazz,String name) throws UnsupportedEncodingException {
        String url = URLDecoder.decode(clazz.getResource("").getPath() + name, "utf-8");
        url = url.replace("/target/classes", "/src/main/java");
        return new Path(url);
    }
}
