package cn.hp.project._01_hadoop_Offline.visits;

import cn.hp.project._01_hadoop_Offline.pageviews.PageViewsBean;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 输入数据：pageviews模型结果数据
 * 从pageviews模型结果数据中进一步梳理出visit模型
 * sessionid  start-time   out-time   start-page   out-page   pagecounts  ......
 */
public class ClickStreamView {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Path input = new Path("C:\\学习\\data\\ClickStreamPageViewOut");
        Path output = new Path("C:\\学习\\data\\ClickStreamViewOut");

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ClickStreamView");

        //1 输入类型
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.setInputPaths(job, input);

        //2 map
        job.setMapperClass(ClickStreamVisitMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(PageViewsBean.class);

        //3分区 4排序 5规约 6分组

        //7 reducer
        job.setReducerClass(ClickStreamVisitReducer.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(VisitBean.class);

        //8输出类型
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, output);

        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);

    }

}


class ClickStreamVisitMapper extends Mapper<LongWritable, Text, Text, PageViewsBean> {

    PageViewsBean pvBean = new PageViewsBean();
    Text k = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String line = value.toString();
        String[] fields = line.split("\001");
        int step = Integer.parseInt(fields[5]);
        //(String session, String remote_addr, String timestr, String request, int step, String staylong, String referal, String useragent, String bytes_send, String status)
        //299d6b78-9571-4fa9-bcc2-f2567c46df3472.46.128.140-2013-09-18 07:58:50/hadoop-zookeeper-intro/160"https://www.google.com/""Mozilla/5.0"14722200
        pvBean.set(fields[0], fields[1], fields[2], fields[3], fields[4], step, fields[6], fields[7], fields[8], fields[9]);
        k.set(pvBean.getSession());
        context.write(k, pvBean);

    }

}


class ClickStreamVisitReducer extends Reducer<Text, PageViewsBean, NullWritable, VisitBean> {

    @Override
    protected void reduce(Text session, Iterable<PageViewsBean> pvBeans, Context context) throws IOException, InterruptedException {

        // 将pvBeans按照step排序
        ArrayList<PageViewsBean> pvBeansList = new ArrayList<PageViewsBean>();
        for (PageViewsBean pvBean : pvBeans) {
            PageViewsBean bean = new PageViewsBean();
            try {
                BeanUtils.copyProperties(bean, pvBean);
                pvBeansList.add(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(pvBeansList, new Comparator<PageViewsBean>() {

            @Override
            public int compare(PageViewsBean o1, PageViewsBean o2) {

                return o1.getStep() > o2.getStep() ? 1 : -1;
            }
        });

        // 取这次visit的首尾pageview记录，将数据放入VisitBean中
        VisitBean visitBean = new VisitBean();
        // 取visit的首记录
        visitBean.setInPage(pvBeansList.get(0).getRequest());
        visitBean.setInTime(pvBeansList.get(0).getTimestr());
        // 取visit的尾记录
        visitBean.setOutPage(pvBeansList.get(pvBeansList.size() - 1).getRequest());
        visitBean.setOutTime(pvBeansList.get(pvBeansList.size() - 1).getTimestr());
        // visit访问的页面数
        visitBean.setPageVisits(pvBeansList.size());
        // 来访者的ip
        visitBean.setRemote_addr(pvBeansList.get(0).getRemote_addr());
        // 本次visit的referal
        visitBean.setReferal(pvBeansList.get(0).getReferal());
        visitBean.setSession(session.toString());

        context.write(NullWritable.get(), visitBean);

    }

}
