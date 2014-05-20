package com.good.akkaserver.server;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

import com.good.akkaserver.event.ClientEvent;

public class AkkaServerTest {

	@Test
	public void test() throws InterruptedException {
		AkkaServer akkaServer = AkkaServer.getInstance();
		assertNotNull(akkaServer.getActorSystem());
//		assertNotNull(akkaServer.getEventEventDispatcherAkkaActor());
		assertNotNull(akkaServer.getPlayerMangerActor());
//		System.out.println(akkaServer.getEventEventDispatcherAkkaActor().path());
		
		ActorSelection actorSelection = akkaServer.getActorSystem().actorSelection("/user/eventDispatch");
		assertNotNull(actorSelection);
		
		
//		ActorRef playerMangerActor = akkaServer.getPlayerMangerActor();
//		ServerEvent event = new ServerEvent();
//		event.setCommand(ProtocolDefine.CREATE_PLAYER_ROLE);
//		event.setActorid(10000);
//		playerMangerActor.tell(event, akkaServer.getPlayerMangerActor());
		
//		Thread.sleep(1000);
//		
//		
//		ActorSelection actorSelection2 = akkaServer.getActorSystem().
//				actorSelection("/user/playerActorManger/" + Integer.toString(10000));
//		
//		ActorRef actorRef = akkaServer.getActorSystem().actorFor("/user/playerActorManger/" + Integer.toString(10000));
//		
//		
//		
//		ServerEvent event2 = new ServerEvent();
//		event2.setCommand(ProtocolDefine.GET_PLAYER_ROLE_INFO);
//		
//		if (!actorRef.isTerminated())
//			actorRef.tell(event2, null);
//		else {
//			System.out.println("terminated");
//		}		
//		
		Thread.sleep(5000);
		
		akkaServer.getActorSystem().shutdown();
	
		
	}

}
