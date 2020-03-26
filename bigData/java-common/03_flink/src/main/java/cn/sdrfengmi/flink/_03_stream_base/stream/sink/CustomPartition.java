package cn.sdrfengmi.flink._03_stream_base.stream.sink;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomPartition implements Partitioner {
    AtomicInteger count = new AtomicInteger(1);

    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        Integer partitionCount = cluster.partitionCountForTopic(topic);
        count.getAndIncrement();
        return count.get() % partitionCount;
    }

    public void close() {
    }

    public void configure(Map<String, ?> configs) {

    }
}
