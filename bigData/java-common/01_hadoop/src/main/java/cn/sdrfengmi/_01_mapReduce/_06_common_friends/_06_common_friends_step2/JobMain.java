package cn.sdrfengmi._01_mapReduce._06_common_friends._06_common_friends_step2;

//import cn.itcast.common_friends_step1.Step1Mapper;
//import cn.itcast.common_friends_step1.Step1Reducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import static cn.sdrfengmi._01_mapReduce.HdfsUtil.*;

public class JobMain extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        //1:获取Job对象
        Job job = Job.getInstance(super.getConf(), "common_friends_step2_job");

        //2:设置job任务
        //第一步:设置输入类和输入路径
        job.setInputFormatClass(TextInputFormat.class);
//            TextInputFormat.addInputPath(job, new Path("file:///E:\\out\\common_friends_step1_out"));
//        TextInputFormat.addInputPath(job,getLocalInputSourcePath(JobMain.class));
        TextInputFormat.addInputPath(job, new Path("01_datasetOut/friendsOut.txt"));

        //第二步:设置Mapper类和数据类型
        job.setMapperClass(Step2Mapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        //第三,四,五,六

        //第七步:设置Reducer类和数据类型
        job.setReducerClass(Step2Reducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //第八步:设置输出类和输出的路径
            /*job.setOutputFormatClass(TextOutputFormat.class);
            TextOutputFormat.setOutputPath(job, new Path("file:///E:\\out\\common_friends_step3_out"));*/
        /*TextOutputFormat.setOutputPath(job, new Path("file:///E:\\out\\common_friends_step4_out"));*/
//        FileOutputFormat.setOutputPath(job, new Path("file:///E:\\out\\common_friends_step5_out"));
//        FileOutputFormat.setOutputPath(job, new Path("file:///C:\\out\\flowpartiton_out" + Math.random()));
        FileOutputFormat.setOutputPath(job, new Path("01_datasetOut/friendsOut.txt" + Math.random()));


        //3:等待job任务结束
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
