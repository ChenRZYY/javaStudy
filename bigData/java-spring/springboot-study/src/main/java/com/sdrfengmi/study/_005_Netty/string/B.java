package com.sdrfengmi.study._005_Netty.string;

public class B extends A {
    
    @Override
    public int getA() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    protected int getB() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public static void main(String[] args) {
        
        A a = new A() {
            @Override
            public int getA() {
                return 0;
            }
            
            @Override
            protected int getB() {
                return 0;
            }
        };
        int a2 = a.getA();
        int b = a.getB();
        
    }
}
