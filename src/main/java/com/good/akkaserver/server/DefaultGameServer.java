package com.good.akkaserver.server;

public class DefaultGameServer extends GameServer {
	
	CommServer socketServer = CommServer.getInstance();

	@Override
	public void serverStart() {
		System.out.println("the game server starts");
	}

	@Override
	public void serverStop() {
		socketServer.stopServer();
		System.out.println("the game server stop");
	}

	@Override
	public void doSomething(String str) {
		// TODO Auto-generated method stub

	}
	


}
