package com.good.akkaserver.message;

import org.apache.commons.pool.PoolableObjectFactory;

public class MidSendMessageFactory implements PoolableObjectFactory{
	
	public static final int MAX_LEN = 5120;
	
	public MidSendMessageFactory(){
		super();
	}
	
	public void activateObject(Object arg0) throws Exception {
		
	}

	public void destroyObject(Object arg0) throws Exception {
		SendMessage msg = (SendMessage)arg0;
		msg.setConData(null);
		msg = null;
	}

	public Object makeObject() throws Exception {
		SendMessage msg = new SendMessage(MAX_LEN);
		return msg;
	}

	public void passivateObject(Object arg0) throws Exception {
		SendMessage msg = (SendMessage)arg0;
		msg.clean();
		msg.setChannelHandlerContent(null);
		msg.setCommand(0);
		msg.setMsgLen(0);
		msg.setPlayerid(0);
	}

	public boolean validateObject(Object arg0) {
		return true;
	}

}
