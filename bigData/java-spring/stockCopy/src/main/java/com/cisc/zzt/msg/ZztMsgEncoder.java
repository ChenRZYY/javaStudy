package com.cisc.zzt.msg;

import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zzt.msg.encrypt.ZztMsgEncrypt;
import com.cisc.zzt.msg.encrypt.ZztMsgEncryptDefaultImpl;
import com.cisc.zzt.msg.exception.MessageFieldTooBigException;
import com.google.common.io.LittleEndianDataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ZztMsgEncoder {
    private static final Logger log = LoggerFactory.getLogger(ZztMsgEncoder.class);
    private Charset charset = Charset.forName("GBK");
    private ZztMsgEncrypt encrypt = new ZztMsgEncryptDefaultImpl();
    private ZztMsg message = new ZztMsg();

    public ZztMsgEncoder(ZztMsg message) {
        this.message = message;
    }

    public byte[] encode() throws IOException {
        int bodylen = 0;
        ByteArrayOutputStream byteData = new ByteArrayOutputStream();
        LittleEndianDataOutputStream output = new LittleEndianDataOutputStream((OutputStream) byteData);
        if (this.message.getAction() > 0) {
            this.message.putInt("Action", this.message.getAction());
            byte[] encryptedBody = this.getEncryptedBodyBytes();
            bodylen = encryptedBody.length + this.message.getHandleSerialNo().getBytes(this.charset).length + 1 + 9;
            output.writeShort(1975);
            output.writeInt(bodylen);
            output.writeInt(this.message.getAction());
            output.writeByte((int) this.message.getForwardCount());
            output.writeInt(this.message.getHandleSerialNo().getBytes(this.charset).length);
            output.write(this.message.getHandleSerialNo().getBytes(this.charset));
            output.writeByte(0);
            output.write(encryptedBody);
        } else {
            output.writeShort(1975);
            output.writeInt(bodylen);
        }
        return byteData.toByteArray();
    }

    private byte[] getEncryptedBodyBytes() {
        byte[] body = this.getBodyBytes();
        return this.encrypt.encode(body);
    }

    private byte[] getBodyBytes() {
        ByteArrayOutputStream byteData = new ByteArrayOutputStream();
        final LittleEndianDataOutputStream output = new LittleEndianDataOutputStream((OutputStream) byteData);
        this.message.forEach((BiConsumer) new BiConsumer<String, Object>() {

            @Override
            public void accept(String name, Object value) {
                try {
                    this.writeKeyValue(name, value);
                } catch (IOException e) {
                    log.error("encode body error!", (Throwable) e);
                } catch (MessageFieldTooBigException e) {
                    log.error("Message Field too big:" + e.getFieldSize());
                }
            }

            private void writeKeyValue(String name, Object value) throws IOException, MessageFieldTooBigException {
                this.checkFieldName(name);
                this.checkFieldValue(value);
                this.writeKey(name);
                this.writeValue(value);
            }

            private void writeKey(String name) throws IOException {
                byte[] val = Objects.toString(name, "").getBytes(ZztMsgEncoder.this.charset);
                output.writeByte(val.length);
                output.write(val);
            }

            private void writeValue(Object value) throws IOException, MessageFieldTooBigException {
                byte[] val = Objects.toString(value, "").getBytes(ZztMsgEncoder.this.charset);
                output.writeInt(val.length);
                output.write(val);
            }

            private void checkFieldName(String name) throws MessageFieldTooBigException {
                byte[] valBytes = Objects.toString(name, "").getBytes(ZztMsgEncoder.this.charset);
                if (valBytes.length > 256 || valBytes.length <= 0) {
                    throw new MessageFieldTooBigException(valBytes.length);
                }
            }

            private void checkFieldValue(Object value) throws MessageFieldTooBigException {
                byte[] valBytes = Objects.toString(value, "").getBytes(ZztMsgEncoder.this.charset);
                if (valBytes.length > 10485760) {
                    throw new MessageFieldTooBigException(valBytes.length);
                }
            }
        });
        return byteData.toByteArray();
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public ZztMsgEncrypt getEncrypt() {
        return this.encrypt;
    }

    public void setEncrypt(ZztMsgEncrypt encrypt) {
        this.encrypt = encrypt;
    }

}