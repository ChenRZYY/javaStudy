package com.cisc.zzt.msg.exception;

public class MessageIncompleteException extends Exception {
    private static final long serialVersionUID = 1L;
    private int expectedLen;
    private int actualLen;

    public MessageIncompleteException(int expectedLen, int actualLen) {
        this.actualLen = actualLen;
        this.expectedLen = expectedLen;
    }

    public String toString() {
        return "Message Expected length:" + this.expectedLen + ",actual length:" + this.actualLen;
    }
}