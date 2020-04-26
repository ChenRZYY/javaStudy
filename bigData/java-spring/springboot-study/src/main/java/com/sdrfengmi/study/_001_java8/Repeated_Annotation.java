package com.sdrfengmi.study._001_java8;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

import static org.junit.Assert.*;//必须是static


@Slf4j()
public class Repeated_Annotation {

    //Supplier  空入参 function
    //Predicate  进行过滤判断
    //Consumer  只有一个accept 方法
    @Test
    public void annotation() {
        Filter[] filters = Filterable.class.getAnnotationsByType(Filter.class);//1.8新增加的api
        for (Filter filter : filters) {
            System.err.println(filter.value());
        }
    }


    @Test
    public void option() {

        //option 解决null问题 Present 现存的当前的
        Optional<Filter> emptyOpt = Optional.empty();
        User user = new User("john@gmail.com", "1234");

        System.out.println("emptyOpt Name is set? " + emptyOpt.isPresent());
        Optional<Object> opt = Optional.of(new Object()); //只能不为null才行
        System.out.println("opt Name is set? " + emptyOpt.isPresent());

        Optional<String> fullName = Optional.ofNullable(null); //可以为null,可以不为null
        System.out.println("Full Name is set? " + fullName.isPresent());
//        System.out.println("Full Name is set? " + fullName.get()); //get() 为null时候会出现 NoSuchElementException
        System.out.println("Full Name: " + fullName.orElseGet(() -> "[none]")); // orElseGet() 这个方法会在有值的时候返回值，如果没有值，它会执行作为参数传入的 Supplier(供应者) 函数式接口，并将返回其执行结果：

        opt.ifPresent(u -> System.out.println(u + "陈振东"));  //如果不为空执行函数式方法

        System.out.println(fullName.map(s -> "Hey " + s + "!").orElse("Hey Stranger!")); //不为null的时候执行 map 否则执行 orElse

        String position = Optional.ofNullable(user).flatMap(u -> u.getPosition()).orElse("default"); //返回去包装的Optional

        User result = Optional.ofNullable(user).orElse(createNewUser("orElse")); //fixme orElse不管 是否为空都会执行() 方法
        User result2 = Optional.ofNullable(user).orElseGet(() -> createNewUser("orElseGet")); //为null 才执行

        User result3 = Optional.ofNullable(user).orElseThrow(() -> new IllegalArgumentException()); //为null时候抛出异常


        String email = Optional.ofNullable(user).map(u -> u.getEmail()).orElse("default@gmail.com");
    }

    @Test
    public void filter() {
        User user = new User("anna@gmail.com", "1234");
        Optional<User> result = Optional.ofNullable(user).filter(u -> u.getEmail() != null && u.getEmail().contains("@")); //不能通过过滤为 null
        assertTrue(result.isPresent());
    }


    private User createNewUser(String name) {
        log.debug(name);
        return new User("extra@gmail.com", "1234");
    }

    @Data
    public class User {
        private String email;
        private String count;
        private String position;

        public User(String email, String count) {
            this.email = email;
            this.count = count;
        }

        public Optional<String> getPosition() {
            return Optional.ofNullable(position);
        }

    }

    //同一类型注解
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Filters {
        Filter[] value();
    }

    //没有Repeatable这个注解,不能在一个类上用多个同样注解
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Filters.class)
    public @interface Filter {
        String value();
    }

    @Filter("filter1")
    @Filter("filter2")
    public interface Filterable {

    }

}
