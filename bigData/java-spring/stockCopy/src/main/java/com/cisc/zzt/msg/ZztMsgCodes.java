package com.cisc.zzt.msg;

import com.cisc.zzt.msg.exception.MessageFieldTooBigException;
import com.cisc.zzt.msg.exception.MessageIncompleteException;
import com.cisc.zzt.msg.exception.MessageTooBigException;
import com.cisc.zzt.msg.exception.ProtoNotSupportedException;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZztMsgCodes {
    private static final Logger log = LoggerFactory.getLogger(ZztMsgCodes.class);
    public static final int MAX_MESSAGE_SIZE = 10485760;
    public static final int MIN_MESSAGE_SIZE = 6;
    public static final int MAX_FIELDNAME_SIZE = 256;
    public static final int MAX_FIELDVALUE_SIZE = 10485760;
    public static final int MESSAGE_BODY_SEPERATOR = 0;
    public static final short MESSAGE_VERSION = 1975;
    public static final String MESSAGE_DEFAULT_ENCODING = "GBK";

    public static final byte[] encode(ZztMsg message) throws IOException {
        return (new ZztMsgEncoder(message)).encode();
    }

    public static ZztMsg decode(InputStream input) throws IOException, MessageIncompleteException,
            ProtoNotSupportedException, MessageTooBigException, MessageFieldTooBigException {
        return (new ZztMsgDecoder()).decode(input);
    }

    public static boolean isReadable(InputStream input) throws IOException {
        return ZztMsgDecoder.isReadable(input);
    }
}