package cn.sdrfengmi._01_mapReduce._01_mapReducerDome;

import cn.sdrfengmi._01_mapReduce.HdfsUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

@SuppressWarnings("all")
public class JobMain extends Configured implements Tool {

    static {
        try {
//            deleteHdfsInputSourceFile(JobMain.class); //TODO 每次创建之前都先删除原来结果
//            uploadHdfsInputSourceFile(JobMain.class); //TODO 上传resource文件
//            deleteHdfsOutputSourceFile(JobMain.class); //TODO 删除out文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * https://www.cnblogs.com/hipercomer/p/4516581.html
     * 阶段1：input/Split/Read/map/partition/sort/spill/merge
     * 1. input: TextInputFormat这个类继承自FileInputFormat，FileInputFormat这个类继承自InputFormat，InputFormat这个类会将文件file按照逻辑进行划分，划分成的每一个split切片将会被分配给一个Mapper任务,文件先被切分成split块，而后每一个split切片对应一个Mapper任务
     * 2. Read: MapTask通过用户编写的RecordReader,从输入InputSplit中解析出一个个key/value,并key/value/Partition写入到内存缓冲区中
     * 3. Map: 该节点主要是将解析出的key/value交给用户编写map()函数处理，并产生一系列新的key/value。
     * 4. Collect: 收集阶段在用户编写map()函数中，当数据处理完成后，一般会调用OutputCollector.collect()输出结果
     * 5. hashPartition: 数据进行hash分区,RangePartition 区间分区
     * 6. sort: 排序时先按照Partition进行排序，再按照key进行排序，默认排序算法是快速排序。key.compareTo排序
     * 7. Combine: 如果用户设置了Combiner，则写入文件之前，对每个分区中的数据进行一次聚集操作。
     * 8. spill: 当缓冲区使用量达到一定阀值，将其spill到disk上，spill前，需要进行排序,调用OutputCollector.collect()输出结果
     *     环形缓冲区默认大小是 100M ，但是只写80%，同时map还会为输出操作启动一个守护线程，当到数据达到80%的时候，守护线程开始清理数据，把数据写到磁盘上，这个过程叫 spill 。
     *      8.1 将分区数据的元信息写到内存索引数据结构SpillRecord中，其中每个分区的元信息包括在临时文件中的偏移量、压缩前数据大小和压缩后数据大小。如果当前内存索引大小超过1MB，则将内存索引写到文件output/spillN.out.index中
     *      8.2 当所有数据处理完后，MapTask会将所有临时文件合并成一个大文件，并保存到文件output/file.out中，同时生成相应的索引文件output/file.out.index。
     * 	    8.3 在进行文件合并过程中，MapTask以分区为单位进行合并。对于某个分区，它将采用多轮递归合并的方式。每轮合并io.sort.factor（默认100）个文件，并将产生的文件重新加入待合并列表中，对文件排序后，重复以上过程，直到最终得到一个大文件
     * 注意： 在内存中进行排序时，数据本身不用移动，仅对索引排序即可
     * <p>
     * 阶段2：mapper端merge
     * 1. 对生成的多个spill文件，sort进行归并排序
     * 2. 最终merge归并成一个大文件
     * 注意：
     * 1. 由于每一个spill文件都是按分区和key排序好的，所以归并完的文件也是按分区和key排序好的。
     * 2. 在进行归并的时候，不是一次性的把所有的spill文件归并成一个大文件。而是部分spill文件归并成中间文件，然后中间文件和剩下的spill文件再进行归并。
     * bean对象实现WritableComparable接口重写compareTo方法，就可以实现在reduce分组,相同key分到一组中
     * <p>
     * 阶段3：reducer端copy/merge/reduce/output
     * 1. copy: 当有新的MapTask事件完成时，拷贝线程从指定的机器上面拷贝数据,安分区拷贝数据相同分区放到一起,ReduceTask 启动 Fetcher 线程到已经完成 MapTask 的节点上复制一份属于自己的数据，这些数据默认会保存在内存的缓冲区中，当内存的缓冲区达到一定的阀值的时候，就会将数据写到磁盘之上
     * 2. merge: 来自不同的机器的多个数据文件，需要归并成一个文件.在拷贝文件过程中会进行文件归并操作.
     * 3. Sort: 阶段按照MapReduce语义，用户编写reduce()函数输入数据是按key进行聚集的一组数据。为了将key相同的数据聚在一起，Hadoop采用了基于排序的策略。由于各个MapTask已经实现对自己的处理结果进行了局部排序，因此，ReduceTask只需对所有数据进行一次归并排序即可。
     * 4. reduce: 相同key的value放一起 调用reduce方法
     * 5. output: OutputFormat是MapReduce输出的基类，所有MapReduce输出都实现了 OutputFormat 接口,主要有：TextOutputFormat 、SequenceFileOutputFormat、MultipleOutputs、DBOutputFormat等
     *
     * @param args
     * @return
     * @throws Exception
     */
    //该方法用于指定一个job任务
    @Override
    public int run(String[] args) throws Exception {
        //1:创建一个job任务对象
        Job job = Job.getInstance(super.getConf(), "globalParameter");
        //如果打包运行出错，则需要加该配置
        job.setJarByClass(JobMain.class);
        //2:配置job任务对象(八个步骤)

        //第一步:指定文件的读取方式和读取路径
        job.setInputFormatClass(TextInputFormat.class);
//        TextInputFormat.addInputPath(job, new Path("dataset/wordcount.txt"));
        TextInputFormat.addInputPath(job, HdfsUtil.getInputputFile("wordcount.txt"));
        //TextInputFormat.addInputPath(job, new Path("file:///D:\\mapreduce\\input"));

        //第二步:指定Map阶段的处理方式和数据类型
        job.setMapperClass(WordCountMapper.class);
        //设置Map阶段K2的类型
        job.setMapOutputKeyClass(Text.class);
        //设置Map阶段V2的类型
        job.setMapOutputValueClass(LongWritable.class);


        //第三，四，五，六 采用默认的方式

        //第七步：指定Reduce阶段的处理方式和数据类型
        job.setReducerClass(WordCountReducer.class);
        //设置K3的类型
        job.setOutputKeyClass(Text.class);
        //设置V3的类型
        job.setOutputValueClass(LongWritable.class);
        job.setNumReduceTasks(3); //TODO 可以设置reduce个数
        //第八步: 设置输出类型
        job.setOutputFormatClass(TextOutputFormat.class);
        //设置输出的路径
//        Path path = HdfsUtil.getHdfsOutputPath(JobMain.class);
        TextOutputFormat.setOutputPath(job, HdfsUtil.getNextOutputputFile());
//        TextOutputFormat.setOutputPath(job, HdfsUtil.getOutputputFile(Math.random() + ""));
//        TextOutputFormat.setOutputPath(job, new Path("file:///C:\\out\\flowpartiton_out"+Math.random()));
//        TextOutputFormat.setOutputPath(job, getHdfsOutputPath(JobMain.class));
//        TextOutputFormat.setOutputPath(job, new Path("hdfs://server02:8020/wordcount_out"));
        //TextOutputFormat.setOutputPath(job, new Path("file:///D:\\mapreduce\\output"));
//        HdfsUtil.deleteHDFSfile("hdfs://server02:8020/wordcount_out");
        //等待任务结束
        boolean bl = job.waitForCompletion(true);

        return bl ? 0 : 1;
    }


    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        configuration.set("mapreduce.framework.name", "local");
        configuration.set(" yarn.resourcemanager.hostname", "local");
        //启动job任务
        int run = ToolRunner.run(configuration, new JobMain(), args);
        System.exit(run);

    }


}
