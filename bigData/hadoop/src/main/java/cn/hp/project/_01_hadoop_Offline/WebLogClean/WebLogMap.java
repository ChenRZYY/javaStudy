package cn.hp.project._01_hadoop_Offline.WebLogClean;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WebLogMap extends Mapper<LongWritable, Text, Text, NullWritable> {

    // 用来存储网站url分类数据
    private static Set<String> pages = new HashSet<String>();
    private static Text k = new Text();
    private static NullWritable v = NullWritable.get();

    /**
     * 从外部配置文件中加载网站的有用url分类数据 存储到maptask的内存中，用来对日志数据进行过滤
     * 缓存
     */
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        pages.add("/about");
        pages.add("/black-ip-list/");
        pages.add("/cassandra-clustor/");
        pages.add("/finance-rhive-repurchase/");
        pages.add("/hadoop-family-roadmap/");
        pages.add("/hadoop-hive-intro/");
        pages.add("/hadoop-zookeeper-intro/");
        pages.add("/hadoop-mahout-roadmap/");

    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        WebLogBean bean = WebLogParser.parser(line);
        if (bean != null) {
            // 过滤js/图片/css等静态资源
            WebLogParser.filtStaticResource(bean, pages);
            /* if (!webLogBean.isValid()) return; */
            k.set(bean.toString());
            context.write(k, v);
        }

    }
}
