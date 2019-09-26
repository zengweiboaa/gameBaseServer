package com.angke.game.io.transport.websocket;

import java.util.concurrent.TimeUnit;

import com.angke.common.config.GlobalConfig;
import com.angke.common.constant.GlobalConfigNames;
import com.angke.game.config.ServerInit;
import com.angke.game.io.transport.WebSocketHeartBeatHandlerAdapter;
import com.angke.game.io.transport.HeartContentJson;
import com.angke.game.io.transport.httpfile.HttpRequestHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

	private Class<ChannelHandler>[] wsBusinessHandlerclass;
	private ChannelHandler[] sharableHandlers;
	private boolean isCoverHandlers;

	public static long loadWriterIdleTimeConfig = Long
			.parseLong(GlobalConfig.get(GlobalConfigNames.WEBSOCKET_HEART_WRITETIMEOUT));
	public static final long MIN_WRITER_IDLETIME = 100;

	public static int timeoutCount = Integer
			.parseInt(GlobalConfig.get(GlobalConfigNames.WEBSOCKET_HEART_TIMEOUT_COUNT));
	public static long writerIdleTime = loadWriterIdleTimeConfig > MIN_WRITER_IDLETIME ? loadWriterIdleTimeConfig
			: MIN_WRITER_IDLETIME;
	public static String[] heartBeatMsgContext = GlobalConfig.get(GlobalConfigNames.WEBSOCKET_HEART_CONTEXTS)
			.split(",");

	public static final HeartContentJson heartContentJson = new HeartContentJson(timeoutCount, writerIdleTime - 50);

	public WebSocketServerInitializer(Class<ChannelHandler>[] wsBusinessHandlerclass, ChannelHandler[] sharableHandlers,
			boolean coverHandlers) {

		this.wsBusinessHandlerclass = wsBusinessHandlerclass;
		this.sharableHandlers = sharableHandlers;
		this.isCoverHandlers = coverHandlers;

	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

		ChannelPipeline pipeline = ch.pipeline();

		if (ServerInit.OP_PRINT_LOG) {
			// pipeline.addFirst(new LoggingHandler(LogLevel.ERROR));
		}
		if (!isCoverHandlers) {
			pipeline.addLast(new IdleStateHandler(writerIdleTime * 5, 0, 0, TimeUnit.MILLISECONDS))
					.addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(512 * 1024))
					.addLast(new ChunkedWriteHandler()).addLast(
							new WebSocketHeartBeatHandlerAdapter(timeoutCount, TextWebSocketFrame.class, String.class));
			if (ServerInit.OP_PROVIDE_TESTCLIENT) {
				pipeline.addLast(new HttpRequestHandler("/ws"));
			}
			pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
			if (ServerInit.OP_USE_DEFAULT_HANDLER) {
				pipeline.addLast(new HandshakeSendHeartFormatHandler());
			}

		}
		if (wsBusinessHandlerclass != null && wsBusinessHandlerclass.length != 0) {
			for (int i = 0; i < wsBusinessHandlerclass.length; i++) {
				ChannelHandler newInstance = wsBusinessHandlerclass[i].newInstance();
				pipeline.addLast(newInstance);
			}
		}
		if (sharableHandlers != null && sharableHandlers.length != 0) {
			pipeline.addLast(this.sharableHandlers);
		}
	}

}
