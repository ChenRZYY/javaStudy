package cn.sdrfengmi._01_mapReduce._02_partitioner;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashMap;

public class PartitionerMapper extends Mapper<LongWritable, Text, Text, LongWritable> {


    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //方式1：定义计数器
        Counter counter = context.getCounter("MR_COUNTER", "partition_counter");
        //每次执行该方法，则计数器变量的值加1
        counter.increment(1L);
        String[] split = value.toString().split(",");
        LongWritable longWritable = new LongWritable();
        Text text = new Text();
        HashMap<String, Integer> map = new HashMap<>();
        for (String word : split) {
            if (map.containsKey(word)) {
                map.put(word,map.get(word)+1);
            }else{
                map.put(word, 1);
            }
        }
        map.forEach((k,v)->{
            longWritable.set(v);
            text.set(k);
            try {
                context.write(text,longWritable );
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
