package com.angke.game.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angke.common.constant.GlobalConfigNames;
import com.angke.game.config.HttpServerConfig;
import com.angke.game.config.TcpServerConfig;
import com.angke.game.config.WebSocketServerConfig;
import com.angke.game.io.transport.GameBootStrap;
import com.angke.game.io.transport.httpfile.HttpFileDownLoadServerBootstrap;
import com.angke.game.io.transport.tcp.TcpServerBootstrap;
import com.angke.game.io.transport.websocket.WebSocketServerBootstrap;

public abstract class ServerStartUp {

	private static final Logger LOG = LoggerFactory.getLogger(ServerStartUp.class);
	
	private static void startServer() throws Exception {

		HttpServerConfig httpServerConfig = new HttpServerConfig("Http Server", "/GlobalConfig.properties", GlobalConfigNames.HTTPFILESERVER_OP, GlobalConfigNames.SERVER_HTTP_FILE_PORT);
		GameBootStrap httpServerBootstrap = new HttpFileDownLoadServerBootstrap();
		httpServerConfig.loadParme().loadHandlers(null, null, false).installConfig(httpServerBootstrap).start();;
		
		TcpServerConfig tcpServerConfig = new TcpServerConfig("Tcp Server", "/GlobalConfig.properties", GlobalConfigNames.TCP_OP, GlobalConfigNames.SERVER_TCP_PORT);
		GameBootStrap tcpServerBootstrap = new TcpServerBootstrap();
		tcpServerConfig.loadParme().loadHandlers(null, null, false).installConfig(tcpServerBootstrap).start();
		
		WebSocketServerConfig websocketServerConfig = new WebSocketServerConfig("WebSocket Server", "/GlobalConfig.properties", GlobalConfigNames.WEBSOCKET_OP, GlobalConfigNames.SERVER_WEBSOCKET_PORT);
		GameBootStrap webSocketServerBootstrap = new WebSocketServerBootstrap();
		websocketServerConfig.loadParme().loadHandlers(null, null, false).installConfig(webSocketServerBootstrap).start();
		
//		WebSocketServerConfig websocketServerConfig2 = new WebSocketServerConfig("WebSocket Server", "/GlobalConfig.properties", GlobalConfigNames.WEBSOCKET_OP, "WS2");
//		GameBootStrap webSocketServerBootstrap2 = new WebSocketServerBootstrap();
//		websocketServerConfig2.loadParme().loadHandlers(null, false).installConfig(webSocketServerBootstrap2).start();
//		
//		WebSocketServerConfig websocketServerConfig3 = new WebSocketServerConfig("WebSocket Server", "/GlobalConfig.properties", GlobalConfigNames.WEBSOCKET_OP, "WS3");
//		GameBootStrap webSocketServerBootstrap3 = new WebSocketServerBootstrap();
//		websocketServerConfig3.loadParme().loadHandlers(null, false).installConfig(webSocketServerBootstrap3).start();
		
		LOG.info(">> Start Server SUCCESS! You Can Telnet And To Send Massage For This Server");
	}
	
	public static void startUp() {
		try {
			startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
