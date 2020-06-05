package com.sdrfengmi.springboot._001_start;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Author chenzhendong
 * @create 2020/6/2 13:56
 */
@Component
@Order(100)
public class ApplicationRunnerImpl implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("通过实现ApplicationRunner接口，在spring boot项目启动后打印参数");
        String[] sourceArgs = args.getSourceArgs();
        for (String arg : sourceArgs) {
            System.out.print(arg + " ");
        }
        System.out.println();
    }
}