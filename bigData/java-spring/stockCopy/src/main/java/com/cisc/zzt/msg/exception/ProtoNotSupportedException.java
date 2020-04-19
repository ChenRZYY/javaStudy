package com.cisc.zzt.msg.exception;

public class ProtoNotSupportedException extends Exception {
	private static final long serialVersionUID = 1L;
	private int proto;

	public ProtoNotSupportedException(int proto) {
		this.proto = proto;
	}

	public String toString() {
		return "Protol:" + this.proto + " is not supported.";
	}
}