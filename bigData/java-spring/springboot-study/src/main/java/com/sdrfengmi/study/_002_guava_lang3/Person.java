package com.sdrfengmi.study._002_guava_lang3;

public class Person {
    
    private String name;
    
    public Person(String name) {
        this.name = name;
    }
    
    public static void staticMet(String t) {
        System.out.println(t);
    }
    
    public String call(String string) {
        System.out.println(name);
        System.out.println(string);
        return string;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "test [name=" + name + "]";
    }
}