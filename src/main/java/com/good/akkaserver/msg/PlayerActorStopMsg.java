package com.good.akkaserver.msg;

/**
 * 停止一个PlayerActor
 * @author GengQing
 * 2014-4-4
 */
public class PlayerActorStopMsg implements MsgInterface {

	
	private int sessionID;
	
	public PlayerActorStopMsg(int sessionID) {
		this.sessionID = sessionID;
	}
	
	@Override
	public int getSessionID() {
		return sessionID;
	}

}
