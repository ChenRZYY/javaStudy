package com.cisc.zzt.msg.exception;

public class MessageFieldTooBigException extends Exception {
	private static final long serialVersionUID = 1L;
	private int fieldSize;

	public MessageFieldTooBigException(int fieldSize) {
		this.fieldSize = fieldSize;
	}

	public String toString() {
		return "Message size:" + this.fieldSize + " is too big!";
	}

	public int getFieldSize() {
		return this.fieldSize;
	}
}