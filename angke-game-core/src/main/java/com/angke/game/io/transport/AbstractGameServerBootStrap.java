package com.angke.game.io.transport;

import java.net.InetAddress;

import org.slf4j.Logger;

import com.angke.common.constant.Loggers;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;

public abstract class AbstractGameServerBootStrap extends Thread implements GameBootStrap {

	protected Logger LOG = Loggers.serverLogger;
	protected Boolean serverOp;
	
	protected String serverName;
	protected Integer port;
	protected ServerBootstrap bootstrap;
	protected Class<ChannelHandler>[] wsBusinessHandlerclass;
	protected ChannelHandler[] sharableHandlers;
	protected Boolean isCoverHandlers;
	
	InetAddress inetAddress = getInetAddress();
	
	@Override
	public void InitialContext(Boolean serverOp, String serverName, Integer port, Class<ChannelHandler>[] wsBusinessHandlerclass, ChannelHandler[] sharableHandlers, Boolean isCoverHandlers) {

		this.serverOp = serverOp;
		this.serverName = serverName;
		this.port = port;
		this.wsBusinessHandlerclass = wsBusinessHandlerclass;
		this.sharableHandlers = sharableHandlers;
		this.isCoverHandlers = isCoverHandlers;
	}
	
	@Override
	public synchronized void start() {
		if (serverOp) {
			super.start();
			LOG.info("服务 {}:已启动 访问地址: {}:{}, Or {}:{} ",serverName, inetAddress.getHostName(),port,inetAddress.getHostAddress(),port);
		}
	}
	
	/**
	 * 获取本机IP和localName
	 */
	private static InetAddress getInetAddress() {
		InetAddress ia=null;
        try {
        	ia=InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ia;
	}

}
