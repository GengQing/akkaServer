package com.good.akkaserver.event;

import org.apache.commons.pool.PoolableObjectFactory;

/**
 * 
 * Description:用于生产服务器事件的工厂类，提供给对象池使用
 */
public class ClientEventFactory implements PoolableObjectFactory {
	
	public ClientEventFactory(){
		super();
	}

	@Override
	public void activateObject(Object arg0) throws Exception {
		
	}

	@Override
	public void destroyObject(Object arg0) throws Exception {
		ClientEvent event = (ClientEvent)arg0;
		event.setConData(null);
		event = null;
	}

	@Override
	public Object makeObject() throws Exception {
		ClientEvent event = new ClientEvent();
		return event;
	}

	@Override
	public void passivateObject(Object arg0) throws Exception {
		ClientEvent event = (ClientEvent)arg0;
		event.reset();
		event.setCommand(0);
		event.setRoleid(0);
		event.setPlayerid(0);
		event.setTimestamp(0);
		event.setSessionID(0);
	}

	@Override
	public boolean validateObject(Object arg0) {
		return true;
	}
	
	

}
