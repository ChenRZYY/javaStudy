package com.sdrfengmi.springboot._001_start;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Author chenzhendong
 * @create 2020/6/2 13:56
 * 两种实现方式的不同之处在于run方法中接收的参数类型不一样
 * 指定执行顺序
 * 当项目中同时实现了ApplicationRunner和CommondLineRunner接口时，可使用Order注解或实现Ordered接口来指定执行顺序，值越小越先执行
 * 可以看出上下文完成刷新后，依次调用注册的runners, runners的类型为 ApplicationRunner 或 CommandLineRunner
 *
 */
@Component
@Order(101)
public class CommandLineRunnerImpl implements CommandLineRunner, Ordered {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("通过实现CommandLineRunner接口，在spring boot项目启动后打印参数");
        for (String arg : args) {
            System.out.print(arg + " ");
        }
        System.out.println();
    }

    @Override
    public int getOrder() {
        return 101;
    }
}