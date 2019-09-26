package com.angke.game.io.transport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.MessageLite;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class WebSocketHeartBeatHandlerAdapter extends ChannelInboundHandlerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(WebSocketHeartBeatHandlerAdapter.class);

	private Map<ChannelHandlerContext, Integer> clientOvertimeMap = new ConcurrentHashMap<>();
	private final int MAX_OVERTIME;
	private final Class<?> HEARTBEAT_MSG_TYPE;
	private final Class<?> HEARTBEAT_MSGCONTEXTTYPE;

	private int ms;

	public WebSocketHeartBeatHandlerAdapter(int MAX_OVERTIME, Class<?> heartBeatMsgType,
			Class<?> heartBeatContextType) {
		this.MAX_OVERTIME = MAX_OVERTIME;
		this.HEARTBEAT_MSG_TYPE = heartBeatMsgType;
		this.HEARTBEAT_MSGCONTEXTTYPE = heartBeatContextType;
	}

	private HeartAndNetworkDelayMsg tryParseToHeartMsg(Object msg, Class<?> parseType) {
		if (parseType == String.class) {
			try {
				HeartAndNetworkDelayMsg parseObject = JSON.parseObject((String) msg, HeartAndNetworkDelayMsg.class);
				return parseObject;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	private boolean computationDelay(int time) {

		Long currentTimeMillis = System.currentTimeMillis();
		int tt = (int) (currentTimeMillis % 100000);
		int ms = tt - time;
		if (ms < 0) {
			return false;
		}
		this.ms = ms;
		return true;
	}

	private boolean dealWebSocketHeartMsg(ChannelHandlerContext ctx, WebSocketFrame msg) {
		if (msg instanceof TextWebSocketFrame) {
			TextWebSocketFrame parseContent = (TextWebSocketFrame) msg;
			String text = parseContent.text();
			HeartAndNetworkDelayMsg parseObject = tryParseToHeartMsg(text, String.class);
			if (parseObject != null && parseObject.getT() != 0) {
				boolean computationDelay = computationDelay(parseObject.getT());
				if (computationDelay) {
					// System.err.println("延迟为：" + this.ms);
					// LOG.info("{} 的延迟是 ： {} ", ctx.channel().remoteAddress(), this.ms);
					return true;
				} else {
					return true;
				}
			}
		} else if (msg instanceof BinaryWebSocketFrame) {
			BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) msg;

			ByteBuf buf = binaryWebSocketFrame.content();
		}
		return false;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		clientOvertimeMap.remove(ctx);// 只要接受到数据包，则清空超时次数
		boolean isHeartMsg = false;
		if (msg.getClass() == HEARTBEAT_MSG_TYPE) {// 不是心跳直接放行
			// 是心跳的类型的话，判断心跳类型
			if (msg instanceof WebSocketFrame) {
				boolean dealWebSocketHeartMsg = dealWebSocketHeartMsg(ctx, (WebSocketFrame) msg);
				if (dealWebSocketHeartMsg) {
					isHeartMsg = true;
				}
			} else if (msg instanceof MessageLite) {// protobuf的消息类型

			}
		} else {
			isHeartMsg = false;
		}
		if (isHeartMsg) {
			repairHeartMsg(ctx.channel());
			return;
		} else {
			ctx.fireChannelRead(msg);
		}

	}

	// 回复心跳消息
	private void repairHeartMsg(Channel channel) {
		Long currentTimeMillis = System.currentTimeMillis();
		int tt = (int) (currentTimeMillis % 100000);
		String heart = "{\"t\":" + tt + "}";
		// 心跳回复
		channel.writeAndFlush(new TextWebSocketFrame(heart));
		// System.err.println("心跳消息回复了");

	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// 心跳包检测读超时
		System.err.println(ctx.channel().getClass());
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				int overtimeTimes = clientOvertimeMap.getOrDefault(ctx, 0);
				if (overtimeTimes < MAX_OVERTIME) {
					Channel incoming = ctx.channel();
					// System.err.println("客户端" +incoming.remoteAddress()+ "读超时,请在 " +
					// (MAX_OVERTIME-overtimeTimes) + "个时间周期内重连");
					addUserOvertime(ctx);
				} else {
					// LOG.debug("客户端超时 关闭吧");
					System.out.println(
							"客户端超时 关闭吧,执行时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
					ctx.close();
				}
			}
		}
	}

	private void addUserOvertime(ChannelHandlerContext ctx) {
		int oldTimes = 0;
		if (clientOvertimeMap.containsKey(ctx)) {
			oldTimes = clientOvertimeMap.get(ctx);
		}
		clientOvertimeMap.put(ctx, (int) (oldTimes + 1));
	}

}
