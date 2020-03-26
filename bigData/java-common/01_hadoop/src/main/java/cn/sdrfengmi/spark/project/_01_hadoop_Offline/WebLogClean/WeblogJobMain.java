package cn.sdrfengmi.spark.project._01_hadoop_Offline.WebLogClean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class WeblogJobMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        //1 设置job
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "WeblogJobMain");

        //1 设置输入
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.setInputPaths(job, new Path("file:///C:\\学习\\accesslog.dat"));

        //2 设置map
        job.setMapperClass(WebLogMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        //3分区 4排序 5规约  6分组

        //7 设置reduce
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        //8 设置输出
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path("file:///C:\\学习\\accesslogClean.dat"));

        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }

}


