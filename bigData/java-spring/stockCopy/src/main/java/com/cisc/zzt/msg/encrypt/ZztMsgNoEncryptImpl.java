package com.cisc.zzt.msg.encrypt;

public class ZztMsgNoEncryptImpl implements ZztMsgEncrypt {
	public final byte[] encode(byte[] input) {
		return input;
	}

	public final byte[] decode(byte[] input) {
		return input;
	}
}