package cn.hp._01_mapReduce._02_partitioner;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class PartitionerReducer extends Reducer<Text, LongWritable,Text,LongWritable> {


    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
        //方式2：使用枚枚举来定义计数器
        context.getCounter(Counter.MY_INPUT_RECOREDS).increment(1L);
//        context.write(key, LongWritable.get());

        long count = 0;
        //1:遍历集合，将集合中的数字相加，得到 V3
        for (LongWritable value : values) {
            count += value.get();
        }
        //2:将K3和V3写入上下文中
        context.write(key, new LongWritable(count));
    }

    public static enum  Counter{
        MY_INPUT_RECOREDS,MY_INPUT_BYTES
    }
}
