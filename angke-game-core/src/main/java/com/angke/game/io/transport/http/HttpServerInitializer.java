package com.angke.game.io.transport.http;

import javax.net.ssl.SSLEngine;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServerInitializer extends ChannelInitializer<Channel> {

	private final ChannelHandler[] httpBusinessHandlers;
	private final boolean isCoverHandlers;
	private final SslContext context;
	private final boolean isClient;
	
	public HttpServerInitializer(SslContext context, boolean isClient, ChannelHandler[] httpBusinessHandlers,boolean coverHandlers) {
		this.context = context;
		this.isClient = isClient;
		this.httpBusinessHandlers = httpBusinessHandlers;
		this.isCoverHandlers = coverHandlers;
	}
	
	@Override
	protected void initChannel(Channel ch) throws Exception {

		ChannelPipeline pipeline = ch.pipeline();
		
		if (context != null) {
			SSLEngine engine = context.newEngine(ch.alloc());
			pipeline.addFirst("ssl", new SslHandler(engine));
		}
		
		pipeline.addFirst(new LoggingHandler(LogLevel.INFO));
		if (!isCoverHandlers) {
			if (isClient) {
				pipeline.addLast("codec", new HttpClientCodec());
			} else {
				pipeline.addLast("codec", new HttpServerCodec());
			}
			pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
			pipeline.addLast(new ChunkedWriteHandler());
			pipeline.addLast(new HttpRequestHandler("/index", "aa"));
		}
		if (httpBusinessHandlers != null && httpBusinessHandlers.length != 0) {
			pipeline.addLast(this.httpBusinessHandlers);
		}
	}

}
