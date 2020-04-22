package com.cisc.zzt.msg;

import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zzt.msg.encrypt.ZztMsgEncrypt;
import com.cisc.zzt.msg.encrypt.ZztMsgEncryptDefaultImpl;
import com.cisc.zzt.msg.exception.MessageFieldTooBigException;
import com.cisc.zzt.msg.exception.MessageIncompleteException;
import com.cisc.zzt.msg.exception.MessageTooBigException;
import com.cisc.zzt.msg.exception.ProtoNotSupportedException;
import com.google.common.io.LittleEndianDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ZztMsgDecoder {
    private static final Logger log = LoggerFactory.getLogger(ZztMsgDecoder.class);
    private ZztMsgSource source = new ZztMsgSource();
    private ZztMsgEncrypt encrypt = new ZztMsgEncryptDefaultImpl();
    private ZztMsg message = new ZztMsg();
    private short version = 0;
    private int message_body_size = 0;
    private Charset charset = Charset.forName("GBK");

    ZztMsgDecoder() {
    }

    public static boolean isReadable(InputStream buffer) throws IOException {
        if (Objects.isNull(buffer)) {
            return false;
        }
        if (buffer.available() < 6) {
            return false;
        }
        LittleEndianDataInputStream data = new LittleEndianDataInputStream(buffer);
        data.mark(6);
        data.skipBytes(2);
        int bodylen = data.readInt();
        data.reset();
        return bodylen + 6 <= data.available();
    }

    private void unpackBody()
            throws MessageIncompleteException, IOException, MessageFieldTooBigException, MessageTooBigException {
        if (this.message_body_size == 0) {
            return;
        }
        int bodylen = this.message_body_size;
        this.checkMessageCompleted(bodylen, this.source.available());
        int start = this.source.available();
        this.message.setAction(this.source.readInt());
        this.message.setForwardCount(this.source.readByte());
        this.message.setHandleSerialNo(this.source.readFieldValue());
        this.source.skipBytes(1);
        int end = this.source.available();
        byte[] bodyBuf = new byte[bodylen - (start - end)];
        this.source.readFully(bodyBuf);
        byte[] data = this.encrypt.decode(bodyBuf);
        this.source.render(data);
        while (this.source.available() > 0) {
            String name = this.source.readFieldName();
            String val = this.source.readFieldValue();
            this.message.putString(name, val);
        }
        this.message.putInt("Action", this.message.getAction());
    }

    public ZztMsg decode(InputStream buffer) throws MessageIncompleteException, MessageTooBigException,
            ProtoNotSupportedException, IOException, MessageFieldTooBigException {
        this.checkMessageReadable(buffer);
        this.source.render(buffer);
        this.unpackHead();
        this.unpackBody();
        return this.message;
    }

    public void unpackHead() throws ProtoNotSupportedException, IOException, MessageTooBigException {
        this.version = this.source.readShort();
        this.checkProtoVersion(this.version);
        this.message_body_size = this.source.readInt();
        this.checkMessageSize(this.message_body_size + 6);
    }

    private void checkProtoVersion(short version) throws ProtoNotSupportedException {
        if (1975 != version) {
            throw new ProtoNotSupportedException((int) version);
        }
    }

    private void checkMessageSize(int messageSize) throws MessageTooBigException {
        if (messageSize > 10485760) {
            throw new MessageTooBigException(messageSize);
        }
    }

    private void checkMessageCompleted(int expectedLen, int actualLen)
            throws MessageIncompleteException, MessageTooBigException {
        if (actualLen < expectedLen) {
            log.info("expectedLen：" + expectedLen + "    actualLen：" + actualLen);
            throw new MessageIncompleteException(expectedLen, actualLen);
        }
    }

    private void checkMessageReadable(InputStream buffer) throws MessageIncompleteException, IOException {
        if (Objects.isNull(buffer) || buffer.available() < 6) {
            throw new MessageIncompleteException(6, buffer.available());
        }
    }

    public ZztMsgEncrypt getEncrypt() {
        return this.encrypt;
    }

    public void setEncrypt(ZztMsgEncrypt encrypt) {
        this.encrypt = encrypt;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    class ZztMsgSource {
        private LittleEndianDataInputStream sourceData;

        ZztMsgSource() {
        }

        private void render(byte[] input) {
            this.sourceData = new LittleEndianDataInputStream((InputStream) new ByteArrayInputStream(input));
        }

        private void render(InputStream buffer) {
            this.sourceData = new LittleEndianDataInputStream(buffer);
        }

        private int readInt() throws IOException {
            return this.sourceData.readInt();
        }

        private short readShort() throws IOException {
            return this.sourceData.readShort();
        }

        private byte readByte() throws IOException {
            return this.sourceData.readByte();
        }

        private void readFully(byte[] data) throws IOException {
            this.sourceData.readFully(data);
        }

        private void skipBytes(int n) throws IOException {
            this.sourceData.skipBytes(n);
        }

        private int available() throws IOException {
            return this.sourceData.available();
        }

        private String readFieldName() throws IOException, MessageFieldTooBigException {
            byte fieldLen = this.sourceData.readByte();
            this.checkFieldLength(fieldLen);
            byte[] fieldData = new byte[fieldLen];
            this.sourceData.readFully(fieldData);
            return new String(fieldData, ZztMsgDecoder.this.charset);
        }

        private String readFieldValue() throws IOException, MessageFieldTooBigException {
            int fieldLen = this.sourceData.readInt();
            this.checkFieldLength(fieldLen);
            byte[] fieldData = new byte[fieldLen];
            this.sourceData.readFully(fieldData);
            return new String(fieldData, ZztMsgDecoder.this.charset);
        }

        private void checkFieldLength(int fieldLen) throws MessageFieldTooBigException {
            if (fieldLen > 10485760) {
                throw new MessageFieldTooBigException(fieldLen);
            }
        }
    }

}