package com.zt.handler;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.zt.model.StockRequest;
import com.zt.util.ServerUtil;
import com.zt.util.StockUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

public class StockWebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

	private static final Logger log = LoggerFactory.getLogger(StockWebSocketServerHandler.class);

	private WebSocketServerHandshaker handshaker;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//当有连接接入 	当前channel激活的时候
//	    System.err.println("--有新连接--"+ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//关闭连接的时候调用 -- 当前channel不活跃的时候，也就是当前channel到了它生命周期末
//	       System.err.println("--关闭连接--"+ctx.channel());
	}

	/**
	 * 	当前channel从远端读取到数据
	 * 
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override 
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 传统http接入
		if ((msg instanceof FullHttpRequest)) {
			handlerHttpRequest(ctx, (FullHttpRequest) msg);
		} else if ((msg instanceof WebSocketFrame)) {
			handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
		// 如果http解码失败,返回http异常
		if ((!req.decoderResult().isSuccess()) || (!"websocket".equalsIgnoreCase(req.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, req,
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		//测试连接test
//		if ("test".equalsIgnoreCase(req.headers().get("test"))) {
//			getRequestParams(ctx, req);
//		}
		// 首次创建连接使用http协议完成的,完成握手
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				"ws://" + req.headers().get("Host"), null, false);
		this.handshaker = wsFactory.newHandshaker(req);
		if (this.handshaker == null) { // 无法处理的websocket版本
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else { // 向客户端发送websocket握手,完成握手
			this.handshaker.handshake(ctx.channel(), req);
		}
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
		// 返回应答给客户端
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}

		ChannelFuture f = ctx.channel().writeAndFlush(res);
		// 如果是非Keep-Alive，关闭连接
		if ((!HttpUtil.isKeepAlive(req)) || (res.status().code() != 200)) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * channel read消费完读取的数据的时候被触发,有时候数据太大会发多个包,这时候保证所有的包读完以后再处理
	 * 
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		// 判断是否是关闭链路的指令
		if ((frame instanceof CloseWebSocketFrame)) {
			this.handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		// 用于前端测试是否连接成功 文本方式
		String requestStr = ((TextWebSocketFrame) frame).text();
		
		//用二进制流的方式
//		String requestStr = binary(ctx, frame);
//		log.error("网页端请求入参: "+new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date())+requestStr);
		if (StringUtil.isNullOrEmpty(requestStr)) {
			ctx.channel().writeAndFlush(new TextWebSocketFrame("gg"));
			return;
		}	
		// 判断是否返回服务器站点
		StockRequest stock = JSON.parseObject(requestStr, StockRequest.class);
		if (!StringUtil.isNullOrEmpty(stock.getMessage())) {
			ctx.channel().writeAndFlush(new TextWebSocketFrame(ServerUtil.getServerMsg()));
			return;
		}
		Channel channel = ctx.channel();
		// 取消推送
		String cancel = stock.getCancelAreas();
		if (!StringUtil.isNullOrEmpty(cancel)) {
			StockUtil.cancel(cancel, channel);
		}
		// 进行本次推送数据
		StockUtil.push(stock.getParams(), channel);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("websocket error", cause);
		ctx.close();
	}

	/**
	 * 如果是http的方式请求
	 */
	@SuppressWarnings("unused")
    private Map<String, String> getRequestParams(ChannelHandlerContext ctx, HttpRequest req){

        Map<String, String>requestParams=new HashMap<>();
        // 处理get请求  
        if (req.method() .equals( HttpMethod.GET)) {
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());  
            Map<String, List<String>> parame = decoder.parameters();  
            Iterator<Entry<String, List<String>>> iterator = parame.entrySet().iterator();
            while(iterator.hasNext()){
                Entry<String, List<String>> next = iterator.next();
                requestParams.put(next.getKey(), next.getValue().get(0));
            }
        }
         // 处理POST请求  
        if (req.method() .equals( HttpMethod.POST)) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(  
                    new DefaultHttpDataFactory(false), req);  
            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas(); //
            for(InterfaceHttpData data:postData){
                if (data.getHttpDataType() == HttpDataType.Attribute) {  
                    MemoryAttribute attribute = (MemoryAttribute) data;  
                    requestParams.put(attribute.getName(), attribute.getValue());
                }
            }
        }
        //Netty HTTP请求获取application/json请求参数
        String content = ((HttpContent) req).content().toString(Charset.forName("UTF-8"));
        System.out.println("The body is : " + content); 
    
        return requestParams;
    }
	
    
	/**
	 * 如果是二进制流的方式入参,解析数据
	 */
	public  String binary(ChannelHandlerContext ctx, WebSocketFrame frame){
		System.out.println("The WebSocketFrame is BinaryWebSocketFrame");
		BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
		
		byte[] by = new byte[frame.content().readableBytes()];
		binaryWebSocketFrame.content().readBytes(by);
		ByteBuf buf = Unpooled.buffer();
		buf.writeBytes(by);
		 String str = "";
		    if(buf.hasArray()) { // 处理堆缓冲区
		        str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
		        byte[] array = buf.array();
		    try {
				  str = new String(array,"UTF-8");
//				System.err.println("jieguo"+str2);
//				System.err.println(Arrays.toString(str2.getBytes()));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		        try {
//		        	byte[] array = buf.array();
//		        str = new String(array,"UTF-8");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
		    } else { // 处理直接缓冲区以及复合缓冲区
		        byte[] bytes = new byte[buf.readableBytes()];
		        buf.getBytes(buf.readerIndex(), bytes);
		        str = new String(bytes, 0, buf.readableBytes());
		    }
		    
//		    ByteBuf bf =binaryWebSocketFrame.content();
//	        byte[] byteArray = new byte[bf.capacity()];  
//	        bf.readBytes(byteArray);  
//	        String result = new String(byteArray);
	
		    return str;

	}
	
	/**
	 * 二进制流的方式解析数据
	 */
	public String binary2(ChannelHandlerContext ctx, WebSocketFrame frame) {
		System.out.println("The WebSocketFrame is BinaryWebSocketFrame");
		BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
		byte[] by = new byte[frame.content().readableBytes()];
		binaryWebSocketFrame.content().readBytes(by);
		ByteBuf bytebuf = Unpooled.buffer();
		bytebuf.writeBytes(by);

		return null;
	}
	
	
}
