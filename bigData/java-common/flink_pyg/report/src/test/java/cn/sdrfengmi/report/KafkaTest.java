package cn.sdrfengmi.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional //支持事物，@SpringBootTest 事物默认自动回滚
//@Rollback // 事务自动回滚，不自动回滚@Rollback(false)
public class KafkaTest {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Test
    public void sendMsg() {
        for (int i = 0; i < 10; i++) {
//            kafkaTemplate.send("pyg", "this is test msg");
//            kafkaTemplate.send("pyg", "水电费是否是的");
            ListenableFuture send = kafkaTemplate.send("pyg", "key", "第二次测试了");
            if (send.isDone()) {
                System.err.println("成功了");
            } else {
                try {
                    //不设置时间 main线程直接关闭,消息不能发送成功
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
