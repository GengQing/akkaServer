package com.good.akkaserver.move;

import akka.actor.ActorRef;
import akka.event.japi.ScanningEventBus;

/**
 * 同一个地图上的玩家相互广播自己的状态
 * @author GengQing
 * 2014-4-15
 */
public class GameMap extends ScanningEventBus<ProtocolDataUnit, ActorRef, Integer> { // E, S, C

	@Override
	public int compareClassifiers(Integer arg0, Integer arg1) {
		return arg0.compareTo(arg1);
	}

	@Override
	public int compareSubscribers(ActorRef arg0, ActorRef arg1) {
		return arg0.compareTo(arg1);
	}

	@Override
	public boolean matches(Integer arg0, ProtocolDataUnit arg1) {
		return true;
//		return arg0 != arg1.getSessionID();
	}

	@Override
	public void publish(ProtocolDataUnit arg0, ActorRef arg1) {
		arg1.tell(arg0, ActorRef.noSender());
		
	}

}
