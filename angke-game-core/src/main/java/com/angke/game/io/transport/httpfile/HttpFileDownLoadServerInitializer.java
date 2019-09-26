package com.angke.game.io.transport.httpfile;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileDownLoadServerInitializer extends ChannelInitializer<SocketChannel> {

	private static final String DEFAULT_URL = "/src/";
	
	private ChannelHandler[] httpBusinessHandlers;
	private boolean isCoverHandlers;
	
	public HttpFileDownLoadServerInitializer(ChannelHandler[] httpBusinessHandlers,boolean coverHandlers) {
		this.httpBusinessHandlers = httpBusinessHandlers;
		this.isCoverHandlers = coverHandlers;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addFirst(new LoggingHandler(LogLevel.INFO));
		if (!isCoverHandlers) {
			
			pipeline
			.addLast(new HttpServerCodec())
			.addLast(new HttpObjectAggregator(512*1024))
			.addLast(new ChunkedWriteHandler())
			.addLast(new HttpFileDownLoadServerHandler(DEFAULT_URL))
			;
		}
		if (httpBusinessHandlers != null && httpBusinessHandlers.length != 0) {
			pipeline.addLast(this.httpBusinessHandlers);
		}
	}
	
}

