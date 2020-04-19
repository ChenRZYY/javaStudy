package com.cisc.zztclient;

import com.cisc.zzt.msg.ZztMsg;
import com.cisc.zzt.msg.ZztMsgCodes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgEncoder extends MessageToByteEncoder<ZztMsg> {
	private static Logger log = LoggerFactory.getLogger(MsgDecoder.class);

	protected void encode(ChannelHandlerContext ctx, ZztMsg msg, ByteBuf out) {
		try {
			byte[] data = ZztMsgCodes.encode(msg);
			out.writeBytes(data);
		} catch (IOException var5) {
			log.error("make package error.", var5);
		}

	}
}