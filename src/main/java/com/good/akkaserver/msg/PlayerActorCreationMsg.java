package com.good.akkaserver.msg;

import org.jboss.netty.channel.ChannelHandlerContext;

/** 
 * 创建一个PlayerActor
 * @author GengQing
 * 2014-4-4
 */
public class PlayerActorCreationMsg implements MsgInterface {
	
	private int sessionID;
	
	private ChannelHandlerContext ctx;

	/* (non-Javadoc)
	 * @see com.good.akkaserver.msg.MsgInterface#getSessionID()
	 */
	@Override
	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	} 

}
