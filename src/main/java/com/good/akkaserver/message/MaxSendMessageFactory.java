package com.good.akkaserver.message;

import org.apache.commons.pool.PoolableObjectFactory;

public class MaxSendMessageFactory implements PoolableObjectFactory{
	
	public MaxSendMessageFactory(){
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
		SendMessage msg = new SendMessage();
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
