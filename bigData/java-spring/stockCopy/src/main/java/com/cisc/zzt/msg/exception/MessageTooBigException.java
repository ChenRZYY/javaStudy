package com.cisc.zzt.msg.exception;

public class MessageTooBigException extends Exception {
    private static final long serialVersionUID = 1L;
    private int size;

    public MessageTooBigException(int size) {
        this.size = size;
    }

    public String toString() {
        return "Message size:" + this.size + " is too big!";
    }
}