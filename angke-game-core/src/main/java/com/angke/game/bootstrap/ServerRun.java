package com.angke.game.bootstrap;

import com.angke.game.config.ServerInit;

public class ServerRun extends ServerStartUp {

	public static void main(String[] args) {
		ServerInit.loadSpring("spring-config.xml");
//		ServerInit.loadSpring(ServerConstants.SERVER_SPRING_FILEPATH);
		try {
			startUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
