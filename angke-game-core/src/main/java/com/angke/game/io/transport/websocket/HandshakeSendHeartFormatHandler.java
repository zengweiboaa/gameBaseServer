package com.angke.game.io.transport.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.angke.game.io.transport.httpfile.HttpRequestHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.GlobalEventExecutor;


/**
 * 处理 WebSocket 文本frame
 * 
 * <p>
 * <ul>
 * <li>{@link HandshakeSendHeartFormatHandler}继承自{@link ChannelInboundHandlerAdapter},
 * <li>覆盖的{@link HandshakeSendHeartFormatHandler#handlerAdded}事件处理方法。每当从服务端收到新的客户端
 * 连接时，客户端的{@link Channel} 会存入{@link ChannelGroup}列表中，并通知列表中的其他客户端 {@link Channel},
 * <li>覆盖的{@link ChannelInboundHandlerAdapter#handlerRemoved}事件处理方法。每当从服务端收到客户端
 * 断开时，客户端的{@link Channel} 祸从 {@link ChannelGroup} 列表中移除，并通知列表中的其他客户端 {@link Channel},
 * <li>覆盖的 {@link ChannelInboundHandlerAdapter#channelRead}事件处理方法。每当从服务端读到客户端写入
 * 信息时，将信息转发给其他客户端的 {@link Channel}。
 * <li>覆盖的{@link ChannelInboundHandlerAdapter#channelActive} 事件处理方法。用于服务端监听到客户端活动,
 * <li>覆盖的 {@link ChannelInboundHandlerAdapter#channelInactive}事件处理方法。服务端监听到客户端不活动,
 * <li>{@link ChannelInboundHandlerAdapter#exceptionCaught}事件处理方法是当出现 Throwable 对象才会被调用,
 * 即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时。在大部分情况下，捕获的异常应该被记录下来并且把关联的 {@link Channel} 给关闭掉。
 * 然而这个方法的处理方式会在遇到不同异常的情况下有不同的实现，比如你可能想在关闭连接之前发送一个错误码的响应消息。
 * <ul>总的来说在这里一共做了一下几件事:
 * <li>当WebSocket 与新客户端已成功握手完成，通过写入信息到{@link ChannelGroup}中的  {@link Channel} 来通知所有
 * 连接的客户端，然后添加新 {@link Channel} 到 {@link ChannelGroup}
 * <li>如果接收到 TextWebSocketFrame，调用 retain() ,并将其写、刷新到 {@link ChannelGroup},使所有连接的
 *  WebSocket Channel 都能接收到它。和以前一样，retain() 是必需的，因为当 channelRead()返回时，
 *  {@link TextWebSocketFrame} 的引用计数将递减。由于所有操作都是异步的，writeAndFlush() 可能会在以后完成，我们不希望它来访问无效的引用。
 * </ul>
 * </ul>
 * @author LiangShengxian
 *
 */
public class HandshakeSendHeartFormatHandler extends ChannelInboundHandlerAdapter {

//	public static ChannelGroup webSocketChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private static final Logger LOG = LoggerFactory.getLogger(HandshakeSendHeartFormatHandler.class);
	
	
//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		System.err.println(msg);
//		System.err.println(msg.toString());
//		if (msg instanceof TextWebSocketFrame) {
//			TextWebSocketFrame request = (TextWebSocketFrame) msg;
//			ctx.fireChannelRead(request.text());
//		}
//	}
	
//    @Override
//	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//    	TextWebSocketFrame textMsg = (TextWebSocketFrame)msg;
//    	String text = textMsg.text();
//    	LOG.info("收到文本帧消息: {}", textMsg.text());
////    	if (text.equals("onopen")) {
////    		Channel incoming = ctx.channel();
////    		String heartJsonString = JSON.toJSONString(WebSocketServerInitializer.heartContentJson);
////    		ctx.writeAndFlush(new TextWebSocketFrame(heartJsonString));
////    		
////    		LOG.info("收到 Client:{} onopen消息,推送心跳格式: {}",incoming.remoteAddress(),heartJsonString);
////    	}else {
////    	}
//    	ctx.fireChannelRead(msg);
////    	Dispatcher.submit(ctx.channel(), text);
////    	if (msg instanceof TextWebSocketFrame) {
////		} else {//非文本帧的消息
////		}
//    }

//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		ctx.fireChannelRead(msg);
//	}
	
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        Channel incoming = ctx.channel();
//        
//        LOG.info("Client:{} 加入",incoming.remoteAddress());
////        channels.add(ctx.channel());
////        for (Channel channel : channels) {
////            channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 加入"));
////        }
//        
//    }

    //事件处理
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	//如果是握手成功事件
    	if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
    		//握手成功之后这个连接就不会再有任何的http消息了，移除掉相应的句柄
    		ctx.pipeline().remove(HttpRequestHandler.class);
    		
    		Channel incoming = ctx.channel();
    		String heartJsonString = JSON.toJSONString(WebSocketServerInitializer.heartContentJson);
    		ctx.writeAndFlush(new TextWebSocketFrame(heartJsonString));
    		
		} else {
			super.userEventTriggered(ctx, evt);
		}
    }
    
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        Channel incoming = ctx.channel();
//        LOG.info("Client:{} 离开",incoming.remoteAddress());
//        webSocketChannels.remove(ctx.channel());
////        for (Channel channel : channels) {
////            channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 离开"));
////        }
//    }

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        Channel incoming = ctx.channel();
//        LOG.info("Client:{} 在线",incoming.remoteAddress());
//    }

//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        Channel incoming = ctx.channel();
//        LOG.info("Client:{} 掉线",incoming.remoteAddress());
//    }

//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
//            throws Exception {
//        Channel incoming = ctx.channel();
//        LOG.info("Client:{} 异常",incoming.remoteAddress());
//        // 当出现异常就关闭连接
//        cause.printStackTrace();
//        ctx.close();
//    }
}
