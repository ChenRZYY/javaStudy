package cn.sdrfengmi._01_mapReduce._06_common_friends._06_common_friends_step1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import static cn.sdrfengmi._01_mapReduce.HdfsUtil.*;

/**
 * 对于 friends理解,
 * A:B,C,D,F,E,O
 * B:F,E,Z,O
 * G:C,F
 * Q:S
 *
 * 第一次map
 * B:---->A
 * C:---->A
 * D:---->A
 * F:---->A
 * E:---->A
 * O:---->A
 * F:---->B
 * E:---->B
 * Z:---->B
 * C:---->G
 * F:---->G
 * S:---->Q
 *
 * 第一次redducer
 * B:---->A-
 * C:---->G-A-
 * D:---->A-
 * E:---->B-A-
 * F:---->G-B-A-
 * O:---->A-
 * S:---->Q-
 * Z:---->B-
 *
 * 也可以按照直接自己的想法 A:B,C,D,F,E,O   A->B  A->C A->D 组合,但是这需要全量的数据才能查询出来完,并且不能针对只查询当前数组的组合A B G Q 这几个元素的friends
 *
 */
public class JobMain extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        //1:获取Job对象
        Job job = Job.getInstance(super.getConf(), "common_friends_step1_job");

        //2:设置job任务
        //第一步:设置输入类和输入路径
        job.setInputFormatClass(TextInputFormat.class);
//        TextInputFormat.addInputPath(job,getLocalInputSourcePath(JobMain.class));
        TextInputFormat.addInputPath(job, new Path("01_dataset/friends.txt"));

        //第二步:设置Mapper类和数据类型
        job.setMapperClass(Step1Mapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        //第三,四,五,六

        //第七步:设置Reducer类和数据类型
        job.setReducerClass(Step1Reducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //第八步:设置输出类和输出的路径
        job.setOutputFormatClass(TextOutputFormat.class);
//        TextOutputFormat.setOutputPath(job, new Path("file:///E:\\out\\common_friends_step1_out"));
//        TextOutputFormat.setOutputPath(job, new Path("file:///C:\\out\\flowpartiton_out" + Math.random()));
//        TextOutputFormat.setOutputPath(job, new Path("file:///C:\\out\\flowpartiton_out" + Math.random()));
        TextOutputFormat.setOutputPath(job, new Path("01_datasetOut/friendsOut.txt"));

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
