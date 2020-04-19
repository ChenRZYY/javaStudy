package com.cisc.zztclient;

import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zzt.msg.ZztMsgCodes;
import com.cisc.zzt.msg.exception.MessageFieldTooBigException;
import com.cisc.zzt.msg.exception.MessageIncompleteException;
import com.cisc.zzt.msg.exception.MessageTooBigException;
import com.cisc.zzt.msg.exception.ProtoNotSupportedException;
import com.cisc.zztclient.RecvTimeoutException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgDecoder extends ByteToMessageDecoder {
	private static final Logger log = LoggerFactory.getLogger(MsgDecoder.class);
	private long msgStartTime = 0L;
	private static final long MAX_SEND_TIME = 705032704L;

	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		try {
			ByteBufInputStream data = new ByteBufInputStream(in);
			while (ZztMsgCodes.isReadable((InputStream) data)) {
				ZztMsg msg = ZztMsgCodes.decode((InputStream) data);
				out.add((Object) msg);
				this.msgStartTime = 0L;
			}
			if (in.readableBytes() > 0) {
				if (this.msgStartTime > 0L) {
					long timeEclapsed = System.currentTimeMillis() - this.msgStartTime;
					if (timeEclapsed > 705032704L) {
						throw new RecvTimeoutException();
					}
				} else {
					this.msgStartTime = System.currentTimeMillis();
				}
			}
		} catch (IOException e) {
			log.error("Read package error.", (Throwable) e);
			in.discardReadBytes();
			ctx.channel().close();
		} catch (MessageTooBigException e) {
			log.error("Message to big.", (Throwable) e);
			in.discardReadBytes();
			ctx.channel().close();
		} catch (ProtoNotSupportedException e) {
			log.error("protol not supported.", (Throwable) e);
			in.discardReadBytes();
			ctx.channel().close();
		} catch (MessageIncompleteException e) {
			log.error("Message incompleted.", (Throwable) e);
			in.discardReadBytes();
			ctx.channel().close();
		} catch (RecvTimeoutException e) {
			log.error("RecvMessage Timeout.", (Throwable) e);
			in.discardReadBytes();
			ctx.channel().close();
		} catch (MessageFieldTooBigException e) {
			log.error("message too big.", (Throwable) e);
			in.discardReadBytes();
			ctx.channel().close();
		}
	}
}