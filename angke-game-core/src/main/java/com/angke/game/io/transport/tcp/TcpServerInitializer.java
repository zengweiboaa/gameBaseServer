package com.angke.game.io.transport.tcp;

import java.util.concurrent.TimeUnit;

import com.angke.game.io.transport.WebSocketHeartBeatHandlerAdapter;
import com.angke.game.io.transport.codec.CustomProtoBufDecoder;
import com.angke.game.io.transport.codec.CustomProtoBufEncoder;
import com.google.protobuf.MessageLite;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpServerInitializer extends ChannelInitializer<SocketChannel>  {

	private ChannelHandler[] tcpBusinessHandlers;
	private boolean isCoverHandlers;
	
	public TcpServerInitializer(ChannelHandler[] tcpBusinessHandlers,boolean coverHandlers) {
		this.tcpBusinessHandlers = tcpBusinessHandlers;
		this.isCoverHandlers = coverHandlers;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addFirst(new LoggingHandler(LogLevel.INFO));
		if (!isCoverHandlers) {
			pipeline.addLast(new IdleStateHandler(300, 0, 0, TimeUnit.MILLISECONDS));
			pipeline.addLast(new CustomProtoBufDecoder());
			pipeline.addLast(new WebSocketHeartBeatHandlerAdapter(0, MessageLite.class, int.class));
			pipeline.addLast(new CustomProtoBufEncoder());
		}
		if (tcpBusinessHandlers != null && tcpBusinessHandlers.length != 0) {
			pipeline.addLast(this.tcpBusinessHandlers);
		}
	}

}
