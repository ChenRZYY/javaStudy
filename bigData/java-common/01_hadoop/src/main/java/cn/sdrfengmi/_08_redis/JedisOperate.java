package cn.sdrfengmi._08_redis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class JedisOperate {

    private JedisPool jedisPool;

    /**
     * 连接redis
     */
    @Before
    public void connectJedis() {
        //创建数据库连接池对象
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);//设置连接redis的最大空闲数
        jedisPoolConfig.setMaxWaitMillis(5000);//设置连接redis-超时时间
        jedisPoolConfig.setMaxTotal(50);//设置redis连接最大客户端数
        jedisPoolConfig.setMinIdle(5);
        //获取jedis连接池
        jedisPool = new JedisPool(jedisPoolConfig, "node01", 6379);
//        Jedis redis = new Jedis("node01", 6379);
    }


    /**
     * String类型的数据操作
     */
    @Test
    public void stringOperate() {
        //获取redis的客户端
        Jedis jedis = jedisPool.getResource();
        //设置string类型的值
        jedis.set("jedisKey", "jedisValue");
        //获取值
        String jedisKey = jedis.get("jedisKey");
        System.out.println(jedisKey);
        //计数器
        jedis.incr("jincr");
        String jincr = jedis.get("jincr");
        System.out.println(jincr);
        jedis.close();
    }


    /**
     * hash表的操作
     */
    @Test
    public void hashOperate() {
        Jedis resource = jedisPool.getResource();
        resource.hset("jhsetkey", "jmapkey", "jmapValue");
        resource.hset("jhsetkey", "jmapkey2", "jmapvalue2");

        Map<String, String> jhsetkey = resource.hgetAll("jhsetkey");
        for (String s : jhsetkey.keySet()) {
            System.out.println("key值为" + s);
            System.out.println(jhsetkey.get(s));
        }
        //修改数据
        resource.hset("jhsetkey", "jmapkey", "linevalue");
        //获取map当中所有key
        Set<String> jhsetkey1 = resource.hkeys("jhsetkey");
        for (String s : jhsetkey1) {
            System.out.println("所有的key为" + s);
        }

        List<String> jhsetkey2 = resource.hvals("jhsetkey");
        for (String s : jhsetkey2) {
            System.out.println("所有的value值为" + s);
        }
        //将jhsetkey这个数据给删掉
        resource.del("jhsetkey");
        //关闭客户端连接对象
        resource.close();
    }


    /**
     * list集合操作
     */
    @Test
    public void listOperate() {
        Jedis resource = jedisPool.getResource();
        resource.lpush("listkey", "listvalue1", "listvalue2", "listvalue3");
        List<String> listkey = resource.lrange("listkey", 0, 1);
        for (String s : listkey) {
            System.out.println(s);
        }
        System.out.println("===============xxxxxxxxxxxxxxxxxxxx===============");
        //从 右边弹出
        // String listkey1 = resource.rpop("listkey");
        // System.out.println(listkey1);
        String listkey2 = resource.lpop("listkey");
        System.out.println(listkey2);
        resource.close();


    }


    /**
     * 对set集合操作
     */
    @Test
    public void setOperate() {
        Jedis resource = jedisPool.getResource();

        //添加数据
        resource.sadd("setkey", "setvalue1", "setvalue2", "setvalue3");

        //查看数据
        Set<String> setkey = resource.smembers("setkey");
        for (String line : setkey) {

            System.out.println(line);
        }
        resource.srem("setkey", "setvalue2");
        System.out.println("=============================");
        Set<String> setkey2 = resource.smembers("setkey");
        for (String line : setkey2) {
            System.out.println(line);
        }
    }


    /**
     * redis哨兵模式下的代码开发
     */
    @Test
    public void testSentinel() {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setMaxTotal(30);
        jedisPoolConfig.setMaxWaitMillis(3000);
        jedisPoolConfig.setMaxIdle(10);

        HashSet<String> sentinels = new HashSet<>(Arrays.asList("node01:26379", "node02:26379", "node03:26379"));
        /**
         * String masterName, Set<String> sentinels,
         final GenericObjectPoolConfig poolConfig
         */
        JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", sentinels, jedisPoolConfig);
        //获取jedis客户端
        Jedis jedis = sentinelPool.getResource();
        jedis.set("sentinelKey", "sentinelValue");
        jedis.close();
        sentinelPool.close();
    }


    /**
     * redis的集群
     */
    @Test
    public void redisCluster() throws IOException {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setMaxTotal(30);
        jedisPoolConfig.setMaxWaitMillis(3000);
        jedisPoolConfig.setMaxIdle(10);

        HashSet<HostAndPort> hostAndPorts = new HashSet<>();
        hostAndPorts.add(new HostAndPort("node01", 7001));
        hostAndPorts.add(new HostAndPort("node01", 7002));
        hostAndPorts.add(new HostAndPort("node01", 7003));
        hostAndPorts.add(new HostAndPort("node01", 7004));
        hostAndPorts.add(new HostAndPort("node01", 7005));
        hostAndPorts.add(new HostAndPort("node01", 7006));
        //获取jedis  cluster
        JedisCluster jedisCluster = new JedisCluster(hostAndPorts, jedisPoolConfig);

        jedisCluster.set("test", "abc");

        String test = jedisCluster.get("test");
        System.out.println(test);

        jedisCluster.close();

    }

    @After
    public void jedisPoolClose() {
        jedisPool.close();
    }


}
