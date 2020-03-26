package cn.sdrfengmi._01_mapReduce._02_partitioner;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class MyPartitioner extends Partitioner<Text, LongWritable> {

    @Override
    public int getPartition(Text text, LongWritable longWritable, int i) {
        if(text.toString().length() >=5 ){
            return  0;
        }else{
            return 1;
        }
    }
}
