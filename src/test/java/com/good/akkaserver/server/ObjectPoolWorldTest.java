package com.good.akkaserver.server;

import static org.junit.Assert.*;

import org.junit.Test;

import com.good.akkaserver.event.ClientEvent;

public class ObjectPoolWorldTest {

	@Test
	public void testGetClientEvent() {
		
		ObjectPoolWorld objectPoolWorld = ObjectPoolWorld.getInstance();
		ClientEvent event = objectPoolWorld.getClientEvent();
		assertNotNull(event);
		ClientEvent event2 = new ClientEvent();
		objectPoolWorld.returnClientEvent(event2);
		
	}

}
