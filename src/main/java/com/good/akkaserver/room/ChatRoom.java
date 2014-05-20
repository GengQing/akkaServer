package com.good.akkaserver.room;

import akka.actor.ActorRef;
import akka.event.japi.ScanningEventBus;

/** 聊天室 */
public class ChatRoom extends ScanningEventBus<ChatEvent, ActorRef, ChatClassifier> { //	// E, S, C

	@Override
	public int compareClassifiers(ChatClassifier arg0, ChatClassifier arg1) {
		return arg0.compareTo(arg1);
	}

	@Override
	public int compareSubscribers(ActorRef arg0, ActorRef arg1) {
		return arg0.compareTo(arg1);
	}

	@Override
	public boolean matches(ChatClassifier arg0, ChatEvent arg1) {
		
		
		return true;
		/*
		int roleId = arg0.getRoleId();
		int from = arg1.getFrom();
		if (from == roleId) return false;
		
		byte type = arg1.getType();
		int to = arg1.getTo();
		switch (type) {
		case ChatEvent.TO_AREAN:		 // 世界聊天
			return true;
		case ChatEvent.TO_COUNTRY:		 // 国家聊天
			return arg0.getCountryId() == to;
		case ChatEvent.TO_MAP:			 // 地图聊天
			return arg0.getMapId() == to;
		case ChatEvent.TO_PERSONAL:		 // 私聊
			return arg0.getRoleId() == to;
		default:
			break;
		}

		return false;
		*/
	}

	@Override
	public void publish(ChatEvent arg0, ActorRef arg1) {
		arg1.tell(arg0, ActorRef.noSender());
	}



}
