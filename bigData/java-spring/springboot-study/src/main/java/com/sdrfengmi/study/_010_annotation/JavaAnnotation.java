package com.sdrfengmi.study._010_annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author Haishi
 * @create 2020/4/23 21:21
 */
public class JavaAnnotation {

    /**
     * 元注解
     *     @Retention - 标识这个注解怎么保存，是只在代码中，还是编入class文件中，或者是在运行时可以通过反射访问。
     *     @Documented - 标记这些注解是否包含在用户文档中。
     *     @Target - 标记这个注解应该是哪种 Java 成员。
     *     @Inherited - 标记这个注解是继承于哪个注解类(默认 注解并没有继承于任何子类)
     *
     * java8注解
     *     @SafeVarargs - Java 7 开始支持，忽略任何使用参数为泛型变量的方法或构造函数调用产生的警告。
     *     @FunctionalInterface - Java 8 开始支持，标识一个匿名函数或函数式接口。
     *     @Repeatable - Java 8 开始支持，标识某注解可以在同一个声明上使用多次。
     *
     *
     *     public interface Annotation
     *
     *     public enum ElementType
     *     TYPE,                类、接口（包括注释类型）或枚举声明
     *     FIELD                字段声明（包括枚举常量）
     *     METHOD,              方法声明
     *     PARAMETER,           参数声明
     *     CONSTRUCTOR,         构造方法声明
     *     LOCAL_VARIABLE,      局部变量声明
     *     ANNOTATION_TYPE,     注释类型声明
     *     PACKAGE              包声明
     *
     *     public enum RetentionPolicy
     *     SOURCE
     *     CLASS
     *     RUNTIME
     *
     * (01) Annotation 就是个接口。
     * "每 1 个 Annotation" 都与 "1 个 RetentionPolicy" 关联，并且与 "1～n 个 ElementType" 关联。可以通俗的理解为：每 1 个 Annotation 对象，都会有唯一的 RetentionPolicy 属性；至于 ElementType 属性，则有 1~n 个。
     *
     * (02) ElementType 是 Enum 枚举类型，它用来指定 Annotation 的类型。
     * "每 1 个 Annotation" 都与 "1～n 个 ElementType" 关联。当 Annotation 与某个 ElementType 关联时，就意味着：Annotation有了某种用途。例如，若一个 Annotation 对象是 METHOD 类型，则该 Annotation 只能用来修饰方法。
     *
     * (03) RetentionPolicy 是 Enum 枚举类型，它用来指定 Annotation 的策略。通俗点说，就是不同 RetentionPolicy 类型的 Annotation 的作用域不同。
     * "每1个Annotation" 都与 "1 个 RetentionPolicy" 关联。
     *
     * a) 若 Annotation 的类型为 SOURCE，则意味着：Annotation 仅存在于编译器处理期间，编译器处理完之后，该 Annotation 就没用了。 例如，" @Override" 标志就是一个 Annotation。当它修饰一个方法的时候，就意味着该方法覆盖父类的方法；并且在编译期间会进行语法检查！编译器处理完后，"@Override" 就没有任何作用了。
     * b) 若 Annotation 的类型为 CLASS，则意味着：编译器将 Annotation 存储于类对应的 .class 文件中，它是 Annotation 的默认行为。
     * c) 若 Annotation 的类型为 RUNTIME，则意味着：编译器将 Annotation 存储于 class 文件中，并且可由JVM读入。
     *   这时，只需要记住"每 1 个 Annotation" 都与 "1 个 RetentionPolicy" 关联，并且与 "1～n 个 ElementType" 关联。学完后面的内容之后，再回头看这些内容，会更容易理解。
     *
     * (01) @interface
     * 使用 @interface 定义注解时，意味着它实现了 java.lang.annotation.Annotation 接口，即该注解就是一个Annotation。
     * 定义 Annotation 时，@interface 是必须的。
     * 注意：它和我们通常的 implemented 实现接口的方法不同。Annotation 接口的实现细节都由编译器完成。通过 @interface 定义注解后，该注解不能继承其他的注解或接口。
     *
     * (02) @Documented
     * 类和方法的 Annotation 在缺省情况下是不出现在 javadoc 中的。如果使用 @Documented 修饰该 Annotation，则表示它可以出现在 javadoc 中。
     * 定义 Annotation 时，@Documented 可有可无；若没有定义，则 Annotation 不会出现在 javadoc 中。
     *
     * (03) @Target(ElementType.TYPE)
     * 前面我们说过，ElementType 是 Annotation 的类型属性。而 @Target 的作用，就是来指定 Annotation 的类型属性。
     * @Target(ElementType.TYPE) 的意思就是指定该 Annotation 的类型是 ElementType.TYPE。这就意味着，MyAnnotation1 是来修饰"类、接口（包括注释类型）或枚举声明"的注解。
     * 定义 Annotation 时，@Target 可有可无。若有 @Target，则该 Annotation 只能用于它所指定的地方；若没有 @Target，则该 Annotation 可以用于任何地方。
     *
     * (04) @Retention(RetentionPolicy.RUNTIME)
     * 前面我们说过，RetentionPolicy 是 Annotation 的策略属性，而 @Retention 的作用，就是指定 Annotation 的策略属性。
     * @Retention(RetentionPolicy.RUNTIME) 的意思就是指定该 Annotation 的策略是 RetentionPolicy.RUNTIME。这就意味着，编译器会将该 Annotation 信息保留在 .class 文件中，并且能被虚拟机读取。
     * 定义 Annotation 时，@Retention 可有可无。若没有 @Retention，则默认是 RetentionPolicy.CLASS。
     *
     */


}
