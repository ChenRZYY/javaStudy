package com.cisc.zzt.msg.encrypt;

public class ZztMsgEncryptDefaultImpl implements ZztMsgEncrypt {
    private static final String MESSAGE_DEFAULT_ENCRYPT_KEY = "25DCFFA558340DE56F6B9C6A0342DD76";
    private static final int MESSAGE_DEFAULT_ENCRYPT_LENGTH = 256;

    public final byte[] encode(byte[] input) {
        return RC4.doRC4(256, input, "25DCFFA558340DE56F6B9C6A0342DD76");
    }

    public final byte[] decode(byte[] input) {
        return RC4.doRC4(256, input, "25DCFFA558340DE56F6B9C6A0342DD76");
    }
}