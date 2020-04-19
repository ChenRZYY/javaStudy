package com.sdrfengmi.study._002_guava_lang3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

public class ReflectUtils {
    
    public static void main(
        String[] args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        
        //1 ConstructorUtils工具类的使用  Accessible 无障碍的 可达到的
        Constructor<Person> accessibleConstructor = ConstructorUtils.getAccessibleConstructor(Person.class, String.class);
        
        Person newInstance = (Person)accessibleConstructor.newInstance("构造方法String入参");
        System.out.println(newInstance.getClass());
        System.out.println(newInstance);

        //2 MethodUtils的使用
        Method accessibleMethod = MethodUtils.getAccessibleMethod(Person.class, "call", String.class);
        Object invoke = accessibleMethod.invoke(newInstance, "参数");
        System.out.println(invoke);
        // 调用静态方法
        MethodUtils.invokeStaticMethod(Person.class, "staticMet", "静态方法");
        
        //3 FieldUtils 暴力获取私有变量(第三个参数表示是否强制获取)---反射方法修改元素的值
        Field field = FieldUtils.getField(Person.class, "name", true);
        field.setAccessible(true);
        System.out.println(field.getType());
        field.set(newInstance, "修改后的值");
        System.out.println(newInstance.getName());
    }
}

