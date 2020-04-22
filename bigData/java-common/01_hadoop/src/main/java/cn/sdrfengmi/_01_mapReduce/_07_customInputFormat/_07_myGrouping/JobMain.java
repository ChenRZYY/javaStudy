package cn.sdrfengmi._01_mapReduce._07_customInputFormat._07_myGrouping;

import cn.sdrfengmi._01_mapReduce.HdfsUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import static cn.sdrfengmi._01_mapReduce.HdfsUtil.getLocalInputSourcePath;

/**
 * WritableComparator排序（类）：
 *     它是用来给Key分组的
 *     它在ReduceTask中进行，默认的类型是GroupingComparator也可以自定义
 *     WritableComparator为辅助排序手段提供基础（继承它），用来应对不同的业务需求
 *     比如GroupingComparator会在ReduceTask将文件写入磁盘并排序后按照Key进行分组，判断下一个key是否相同，将同组的Key传给reduce()执行
 */
public class JobMain extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        //1:获取Job对象
        Job job = Job.getInstance(super.getConf(), "mygroup_job");

        //2:设置job任务
        //第一步:设置输入类和输入路径
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, getLocalInputSourcePath(JobMain.class));

        //第二步:设置Mapper类和数据类型
        job.setMapperClass(GroupMapper.class);
        job.setMapOutputKeyClass(OrderBean.class);
        job.setMapOutputValueClass(Text.class);

        //第三,四,五,六
        //设置分区
        job.setPartitionerClass(OrderPartition.class);
        //设置分组
        job.setGroupingComparatorClass(OrderGroupComparator.class);

        //第七步:设置Reducer类和数据类型
        job.setReducerClass(GroupReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        //第八步:设置输出类和输出的路径
        job.setOutputFormatClass(TextOutputFormat.class);
//            TextOutputFormat.setOutputPath(job, new Path("file:///C:\\out\\mygroup_out"));
        TextOutputFormat.setOutputPath(job, HdfsUtil.getNextOutputputFile());

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
