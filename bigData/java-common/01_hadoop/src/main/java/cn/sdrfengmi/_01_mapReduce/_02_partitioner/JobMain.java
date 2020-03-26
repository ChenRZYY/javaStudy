package cn.sdrfengmi._01_mapReduce._02_partitioner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import static cn.sdrfengmi._01_mapReduce.HdfsUtil.*;


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
    public int run(String[] strings) throws Exception {
        Job job = Job.getInstance(super.getConf(), "_02_partitioner");

        //1 设置输入
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.setInputPaths(job, getHdfsInputputPath(JobMain.class));

        //2 设置map
        job.setMapperClass(PartitionerMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        //3 设置指定分区类
        job.setPartitionerClass(MyPartitioner.class);
        //4,5,6
        //7设置reduce
        job.setReducerClass(PartitionerReducer.class);
        job.setOutputValueClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        //设置reduce个数
        job.setNumReduceTasks(2); //TODO rduce 个数必须和partitioner个数相同

        //8 设置输出
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, getHdfsOutputPath(JobMain.class));
//        HdfsUtil.deleteHDFSfile(HdfsUtil.getOutputStr(Class.class));

        boolean b = job.waitForCompletion(true);
        return b ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        int run = ToolRunner.run(conf, new JobMain(), args);
        System.exit(run);
    }
}
