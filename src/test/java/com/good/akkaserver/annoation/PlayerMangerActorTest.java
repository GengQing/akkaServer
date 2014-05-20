package com.good.akkaserver.annoation;

import static org.junit.Assert.*;
import akka.actor.ActorRef;

import com.good.akkaserver.model.PlayerSession;
import com.good.akkaserver.server.AkkaServer;

import org.junit.Test;

public class PlayerMangerActorTest {

	
	@Test
	public void test() throws InterruptedException {
		AkkaServer akkaServer = AkkaServer.getInstance();
		ActorRef playerActorManger = akkaServer.getPlayerMangerActor();
		PlayerSession playerSession = new PlayerSession();
		playerSession.setId(1);
		playerActorManger.tell(playerSession, ActorRef.noSender());
		Thread.sleep(3000);
		playerActorManger.tell(1, ActorRef.noSender());
		Thread.sleep(3000);
		
		System.out.println("再发一个毒药");
		playerActorManger.tell(1, ActorRef.noSender());
		
		Thread.sleep(3000);
	}

}
