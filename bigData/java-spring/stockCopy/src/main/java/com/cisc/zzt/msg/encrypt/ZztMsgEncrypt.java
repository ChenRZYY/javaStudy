package com.cisc.zzt.msg.encrypt;

public interface ZztMsgEncrypt {
	byte[] encode(byte[] var1);

	byte[] decode(byte[] var1);
}