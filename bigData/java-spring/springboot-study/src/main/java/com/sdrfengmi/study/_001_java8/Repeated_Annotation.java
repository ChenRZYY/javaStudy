package com.sdrfengmi.study._001_java8;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

public class Repeated_Annotation {
    
    
    public static void main(String[] args) {
        Filter[] filters = Filterable.class.getAnnotationsByType(Filter.class);//1.8新增加的api
        for (Filter filter : filters) {
            System.err.println(filter.value());
        }
        
        Optional< String > fullName = Optional.ofNullable( null );
        System.out.println( "Full Name is set? " + fullName.isPresent() );        
        System.out.println( "Full Name: " + fullName.orElseGet( () -> "[none]" ) ); 
        System.out.println( fullName.map( s -> "Hey " + s + "!" ).orElse( "Hey Stranger!" ) );
        
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
    };
    
    @Filter("filter1")
    @Filter("filter2")
    public interface Filterable {
        
        
    }
    
}
