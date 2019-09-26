package com.angke.game.config;

import com.angke.common.config.AbsConfig;
import com.angke.game.io.transport.AbstractGameServerBootStrap;
import com.angke.game.io.transport.GameBootStrap;

import io.netty.channel.ChannelHandler;

public abstract class NetConfigAdapter extends AbsConfig {

	/** 服务名 */
	protected String serverName;
	/** 服务开关 */
	protected boolean serverOp;
	/** 服务开关配置常量名 */
	protected String serverOpName;
	/** 端口配置 */
	protected int port;
	/** 端口配置常量名 */
	protected String portName;
	
	protected Class<ChannelHandler>[] wsBusinessHandlerclass;
	protected ChannelHandler[] sharableHandlers;
	protected Boolean isCoverHandlers;
	
	
	public NetConfigAdapter(String serverName, String filePath, String opName, String portName) {
		super(filePath);
		this.serverName = serverName;
		this.serverOpName = opName;
		this.portName = portName;
	}
	
	public NetConfigAdapter loadParme() {
		this.serverOp = Boolean.parseBoolean(get(serverOpName));
		if (serverOp) {
			port = Integer.parseInt(get(portName));
		}
		return this;
	}
	
	public NetConfigAdapter loadHandlers(Class<ChannelHandler>[] wsBusinessHandlerclass, ChannelHandler[] sharableHandlers, Boolean isCoverHandlers) {
		this.wsBusinessHandlerclass = wsBusinessHandlerclass;
		this.sharableHandlers = sharableHandlers;
		this.isCoverHandlers = isCoverHandlers;
		return this;
	}
	
	public AbstractGameServerBootStrap installConfig(GameBootStrap obj) {
		obj.InitialContext(serverOp, serverName, port, wsBusinessHandlerclass, sharableHandlers, isCoverHandlers);
		return (AbstractGameServerBootStrap) obj;
	}

}
