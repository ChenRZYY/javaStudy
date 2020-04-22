package com.sdrfengmi.study._009_enum;

import org.junit.Test;

/**
 * @Author 陈振东
 * @create 2020/4/21 13:50
 */
public class EnumTest {


    //JDK1.6之前的switch语句只支持int,char,enum类型，使用枚举，能让我们的代码可读性更强。
    @Test
    public void useColor() {
        Color color = Color.RED;
        String name = "";
        switch (color) {
            case RED:
                name = Color.GREEN.getName();
                break;
            case BLANK:
                name = Color.BLANK.getName();
                break;
            case YELLO:
                name = Color.YELLO.getName();
                break;
        }
        System.err.println(name);
    }

    //java.util.EnumSet和java.util.EnumMap是两个枚举集合。
    // EnumSet保证集合中的元素不重复；
    // EnumMap中的 key是enum类型，而value则可以是任意类型。关于这个两个集合的使用就不在这里赘述，可以参考JDK文档。
    // 循环枚举,输出ordinal属性；若枚举有内部属性，则也输出。(说的就是我定义的TYPE类型的枚举的typeName属性)
    // 枚举类型对象之间的值比较，是可以使用==，直接来比较值，是否相等的，不是必须使用equals方法的哟。
    @Test
    public void enumSet() {
        for (Color simpleEnum : Color.values()) {
            //遍历enum
            System.out.println(simpleEnum + "  ordinal  " + simpleEnum.ordinal());
        }
        Color red = Color.valueOf("RED");//根据名字找enum
        System.out.println(red);
    }

    @Test
    public void enumMap() {
    }
}
