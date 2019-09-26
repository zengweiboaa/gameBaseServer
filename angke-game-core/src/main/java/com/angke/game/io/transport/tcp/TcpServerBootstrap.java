package com.angke.game.io.transport.tcp;

import com.angke.game.io.transport.AbstractGameServerBootStrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TcpServerBootstrap extends AbstractGameServerBootStrap {

    public void run() {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        	bootstrap = new ServerBootstrap();
        	bootstrap
        	.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new TcpServerInitializer(sharableHandlers, this.isCoverHandlers))
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
			.childOption(ChannelOption.TCP_NODELAY, true)
            ;


            // 绑定端口，开始接收进来的连接
            ChannelFuture f;
			try {
				f = bootstrap.bind(port).sync();
				System.out.println(serverName +"~~~~~~~~~~~~~");
				// 等待服务器  socket 关闭 。
				f.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

            System.out.println(serverName + " 关闭了");
        }
    }

    
}
