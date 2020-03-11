package cn.hp._01_mapReduce._06_common_friends._06_map_join;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.net.URI;

import static cn.hp._01_mapReduce.HdfsUtil.getLocalInputSourcePath;

public class JobMain extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        //1:获取job对象
        Job job = Job.getInstance(super.getConf(), "map_join_job");

        //2:设置job对象(将小表放在分布式缓存中)
        //将小表放在分布式缓存中
        // DistributedCache.addCacheFile(new URI("hdfs://node01:8020/cache_file/product.txt"), super.getConf());
//        job.addArchiveToClassPath(new Path(""));// 缓存jar包到task运行节点的classpath中
//        job.addFileToClassPath(new Path("")); // 缓存普通文件到task运行节点的classpath中
//        job.addCacheArchive(new URI("")); // 缓存压缩包文件到task运行节点的工作目录
//        job.addCacheFile(new URI("")); // 缓存普通文件到task运行节点的工作目录

//        job.addCacheFile(new URI("file:///C:\\学习\\study\\bigData\\hadoop\\src\\main\\java\\cn\\hp\\_01_mapReduce\\_06_common_friends\\_06_map_join\\product.txt"));
        job.addCacheFile(new URI("hdfs://server02:8020/study/cache_file/product.txt"));  //TODO 缓存小表

        //第一步:设置输入类和输入的路径
        job.setInputFormatClass(TextInputFormat.class);
//        TextInputFormat.addInputPath(job, new Path("hdfs://server02:8020/study/cache_file/product.txt")); ///study/cache_file/product.txt
        TextInputFormat.addInputPath(job, getLocalInputSourcePath(JobMain.class));

        //第二步:设置Mapper类和数据类型
        job.setMapperClass(MapJoinMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        //第八步:设置输出类和输出路径
        job.setOutputFormatClass(TextOutputFormat.class);
//        TextOutputFormat.setOutputPath(job, new Path("file:///D:\\out\\map_join_out"));
        FileOutputFormat.setOutputPath(job, new Path("file:///C:\\out\\flowpartiton_out" + Math.random()));

        //3:等待任务结束
        boolean bl = job.waitForCompletion(true);
        return bl ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        //启动job任务
        int run = ToolRunner.run(configuration, new JobMain(), args);
        System.exit(run);
    }
}
