package com.angke.game.io.transport;

import io.netty.channel.ChannelHandler;

public interface GameBootStrap {

	void InitialContext(Boolean serverOp, String serverName, Integer port,Class<ChannelHandler>[] wsBusinessHandlerclass, ChannelHandler[] businessHandlers, Boolean isCoverHandlers);
}
