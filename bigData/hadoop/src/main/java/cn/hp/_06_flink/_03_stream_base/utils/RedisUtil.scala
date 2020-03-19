package cn.hp._06_flink._03_stream_base.utils

import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

object RedisUtil {
  private val config: FlinkJedisPoolConfig = new FlinkJedisPoolConfig.Builder()
    .setHost("node01").setPort(6379).build()

  def getRedisSink(): RedisSink[(String, Int)] = {
    new RedisSink[(String, Int)](config, new MyRedisMapper)
  }

  class MyRedisMapper extends RedisMapper[(String, Int)] {
    override def getCommandDescription: RedisCommandDescription = {
      new RedisCommandDescription(RedisCommand.HSET, "prefix")
    }

    override def getKeyFromData(data: (String, Int)): String = data._1

    override def getValueFromData(data: (String, Int)): String = data._2.toString
  }

}
