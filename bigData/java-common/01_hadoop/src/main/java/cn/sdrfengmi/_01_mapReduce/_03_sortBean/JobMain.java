package cn.sdrfengmi._01_mapReduce._03_sortBean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import static cn.sdrfengmi._01_mapReduce.HdfsUtil.*;

/**
 * 自定义sortBean  并且实现排序
 */
public class JobMain extends Configured implements Tool {

    static{
        try {
            deleteHdfsInputSourceFile(JobMain.class); //TODO 每次创建之前都先删除原来结果
            uploadHdfsInputSourceFile(JobMain.class); //TODO 上传resource文件
            deleteHdfsOutputSourceFile(JobMain.class); //TODO 删除out文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        //1:创建job对象
        Job job = Job.getInstance(super.getConf(), "mapreduce_sort");

        //2:配置job任务(八个步骤)
        //第一步:设置输入类和输入的路径
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, getHdfsInputputPath(JobMain.class));

//        TextInputFormat.addInputPath(job, new Path("hdfs://server02:8020/globalParameter"));
//        TextInputFormat.addInputPath(job, new Path("hdfs://server02:8020/input/hadoop_03_sortBean_input"));
//        TextInputFormat.addInputPath(job, new Path("hdfs://server02:8020/input/chenzhendong"));
//        TextInputFormat.addInputPath(job, new Path("file:///D:\\input\\sort_input"));
        //第二步: 设置Mapper类和数据类型
        job.setMapperClass(SortMapper.class);
        job.setMapOutputKeyClass(SortBean.class); //自己定义的bean必须实现序列化接口,选择性实现 Comparable
        job.setMapOutputValueClass(NullWritable.class);

        //第三，四，五，六

        //第七步：设置Reducer类和类型
        job.setReducerClass(SortReducer.class);
        job.setOutputKeyClass(SortBean.class);
        job.setOutputValueClass(NullWritable.class);


        //第八步: 设置输出类和输出的路径
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, getHdfsOutputPath(JobMain.class));
//            TextOutputFormat.setOutputPath(job, new Path("file:///D:\\out\\sort_out"));


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