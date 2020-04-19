package com.cisc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.Lists;

public class StockTest {
    
    public static void main(String[] args) throws Exception {
        
        //测试10个参数请求
        //testSingleStock(args);
        
        //测试A股的所有股票订阅
        testMultiStock(args);
        
    }
    
    /**
     * 测试A股所有股票
     * @date 2019年6月17日
     * @author 陈振东
     * @param args
     * @throws IOException
     * @throws InterruptedException 
     * 
     * 启动命令例如
     *  start /b java -jar stockTest.jar 250 1000 10.137.37.93:7399 false >> stockTest.log
        start /b java -jar stockTest.jar 250 1000 192.19.23.80:7392 false >> stockTest.log
     * 
     */
    private static void testMultiStock(String[] args) throws IOException, InterruptedException {
        
        int threadNumber = 100; //线程数量 -->自动计算每个用户分配的线程数量
        int subscribeCount = 200;//订阅数量
        String connect = "192.19.23.80:7391";
        //        String connect = "10.137.37.93:7399";
        //        String connect = "192.19.23.7:7392";
        boolean flag = false;
        
        if (args.length == 1) {
            threadNumber = Integer.parseInt(args[0]);
        }
        if (args.length == 2) {
            threadNumber = Integer.parseInt(args[0]);
            subscribeCount = Integer.parseInt(args[1]);
        }
        else if (args.length == 3) {
            threadNumber = Integer.parseInt(args[0]);
            subscribeCount = Integer.parseInt(args[1]);
            connect = args[2];
        }
        else if (args.length == 4) {
            threadNumber = Integer.parseInt(args[0]);
            subscribeCount = Integer.parseInt(args[1]);
            connect = args[2];
            flag = "true".equals(args[3]) ? true : false;
        }
        
        File stockCodeFile =
            new File(new File("").getAbsoluteFile() + "/testParameter/multiStock/stockCode.properties");
        List<String> stockCodes = getReaderLine(stockCodeFile);
        File parameterFile =
            new File(new File("").getAbsoluteFile() + "/testParameter/multiStock/parameter.properties");
        String parameterReader = getReaderAll(parameterFile);
        int count = 1;
        List<String> parameter = new ArrayList<String>();
        for (String code : stockCodes) {
            parameter.add(parameterReader.replaceAll("oneStock_min_fenshi_000651", "oneStock_min_fenshi_" + code)
                .replaceAll("000651", code));
            if (count > subscribeCount) {
                break;
            }
            count++;
        }
        List<List<String>> parameterGroup = parameterGroup(parameter, subscribeCount / threadNumber);
        //线程池测试多个连接
        ExecutorService pool = Executors.newFixedThreadPool(threadNumber);
        for (int i = 0; i < threadNumber; i++) {
            Thread.sleep(100);
            pool.execute(new WebsocketTest(parameterGroup.get(i), connect, flag));
        }
        
    }
    
    private static List<List<String>> parameterGroup(List<String> parameters, int groupCount) {
        List<List<String>> group = Lists.newArrayList();
        ArrayList<String> parameterGroup = null;
        for (int i = 0; i < parameters.size(); i++) {
            if (i % groupCount == 0) {
                parameterGroup = Lists.newArrayList();
                group.add(parameterGroup);
            }
            parameterGroup.add(parameters.get(i));
        }
        
        return group;
    }
    
    private static List<String> getReaderLine(File file) {
        //        File file = new File(new File("").getAbsoluteFile()+"/testParameter/multiStock/stockCode.properties");  
        ArrayList<String> stockCode = Lists.newArrayList();
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {
                // 显示行号  
                stockCode.add(tempString);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e1) {
                }
            }
        }
        return stockCode;
        
    }
    
    private static String getReaderAll(File file) {
        //        File file = new File(new File("").getAbsoluteFile()+"/testParameter/multiStock/stockCode.properties");  
        StringBuffer parameter = new StringBuffer();
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {
                // 显示行号  
                parameter.append(tempString);
                System.out.println("line " + line + ": " + tempString);
                line++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e1) {
                }
            }
        }
        return parameter.toString();
        
    }
    
    /**
     * //测试10个参数请求-->如果打成jar包运行可以传入3个参数
     * 参数一:线程数量(用户数)    默认是10
     * 参数二:连接的ip:port    默认是 "192.19.23.7:7392"
     * 参数三:是否循环请求                     默认是false
     * 
     * 
     * @date 2019年6月17日
     * @author 陈振东
     * @param args
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private static void testSingleStock(String[] args) throws Exception {
        //测试单个连接
        //      WebsocketTest test = new WebsocketTest();
        //      test.start();
        List<String> parameter = new StockTest().getParameterByLinux();
        //        List<String> parameter = getParameterByWindows();
        int threadNumber = 10;
        String connect = "192.19.23.7:7392";
        boolean flag = false;
        //        String connect="10.137.36.48:7391";
        if (args.length == 1) {
            threadNumber = Integer.parseInt(args[0]);
        }
        else if (args.length == 2) {
            threadNumber = Integer.parseInt(args[0]);
            connect = args[1];
        }
        else if (args.length == 3) {
            threadNumber = Integer.parseInt(args[0]);
            connect = args[1];
            flag = "true".equals(args[2]) ? true : false;
        }
        
        //线程池测试多个连接
        ExecutorService pool = Executors.newFixedThreadPool(threadNumber);
        //        ExecutorService loopPool = Executors.newFixedThreadPool(threadNumber);
        for (int i = 0; i < threadNumber; i++) {
            pool.execute(new WebsocketTest(parameter, connect, flag));
            //            pool.execute(new WebsocketTest(parameter, connect, flag, loopPool));
            //            Thread.sleep(100);
        }
    }
    
    public static List<String> getParameterByWindows() {
        
        //        List<Properties> list = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        //        InputStream inStream = StockTest.class.getClassLoader().getResourceAsStream("parameter1.properties");
        
        //        String t = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String path = StockTest.class.getClassLoader().getResource("log4j.properties").getPath();
        File file = new File(path).getParentFile();
        //        File parentFile2 = file2.getParentFile();
        //        System.err.println("path " + path);
        
        //        InputStream in = StockTest.class.getClass().getResourceAsStream("/userFile.properties");  
        
        //        File parentFile = new File("").getAbsoluteFile();
        //        String[] list = parentFile.list();
        //        System.err.println("parentFile" + list.length);
        //        InputStream in = StockTest.class.getResourceAsStream("log4j.properties");
        
        System.err.println("file " + file);
        String[] filelist = file.list();
        System.err.println("filelist " + filelist);
        for (int i = 0; i < filelist.length; i++) {
            File readfile = new File(filelist[i]);
            System.err.println(readfile.getName());
            if (readfile.getName().startsWith("parameter")) {
                //                File absoluteFile = readfile.getAbsoluteFile();
                //                String absolutePath = file.getAbsolutePath();
                
                String parameter = outInit(file.getAbsolutePath() + "\\" + readfile.getName());
                parameters.add(parameter);
            }
        }
        
        return parameters;
    }
    
    public static String outInit(String outPathStr) {
        //        Properties properties = new Properties();
        
        File parentFile = new File("").getAbsoluteFile();
        //判断当前系统是windows/还是linux 不同的系统获取路径不一样 跑本地时放开
        //        Properties prop = System.getProperties();
        //        String os = prop.getProperty("os.name");
        //        if (os != null && os.toLowerCase().indexOf("windows") > -1) {
        //            parentFile = new File("").getAbsoluteFile().getParentFile();
        //         } 
        
        //        File file = new File(parentFile.getPath(), new File(outPathStr).getPath());
        //        InputStreamReader reader = null;
        FileInputStream fis = null;
        BufferedReader reader1 = null;
        StringBuffer stringBuffer = new StringBuffer();
        
        try {
            //            fis = new FileInputStream(file);
            //            reader = new InputStreamReader(fis,"utf-8");
            //            properties.load(reader);
            
            //返回读取指定资源的输入流
            InputStream in = StockTest.class.getClass().getResourceAsStream(outPathStr);
            in = new FileInputStream(outPathStr);
            
            //            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            //            while((s=br.readLine())!=null)
            //                return s;
            //            byte[] tempbytes = new byte[100];  
            //            int byteread = 0;  
            //            ReadFromFile.showAvailableBytes(in);  
            // 读入多个字节到字节数组中，byteread为一次读入的字节数  
            //            while ((byteread = in.read(tempbytes)) != -1) {  
            ////                String string = new String(tempbytes);
            //                System.out.write(tempbytes, 0, byteread);  
            //                stringBuffer.append(tempbytes.toString());
            //            }  
            
            
            reader1 = new BufferedReader(new FileReader(outPathStr));
            String tempString = null;
            //            int line = 1;
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader1.readLine()) != null) {
                // 显示行号  
                stringBuffer.append(tempString);
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (reader1 != null) {
                    reader1.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }
    
    @SuppressWarnings("resource")
    public List<String> getParameterByLinux() throws IOException {
        String path = StockTest.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "singleStock/";
        System.out.println("path: " + path);
        JarFile localJarFile = new JarFile(new File(path));
        System.err.println("localJarFile" + localJarFile);
        
        ArrayList<String> parameters = new ArrayList<>();
        Enumeration<JarEntry> entries = localJarFile.entries();
        
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            //            System.out.println(jarEntry.getName());
            String innerPath = jarEntry.getName();
            if (innerPath.startsWith("parameter")) {
                InputStream inputStream = StockTest.class.getClassLoader().getResourceAsStream(innerPath);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                StringBuffer strings = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    System.out.println(innerPath + "内容为:" + line);
                    strings.append(line);
                }
                parameters.add(strings.toString());
            }
        }
        return parameters;
    }
}
