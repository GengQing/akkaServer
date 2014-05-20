package com.good.akkaserver.server;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.good.akkaserver.annoation.PlayerMangerActor;
import com.good.akkaserver.event.ClientEvent;
import com.good.akkaserver.move.GameMap;
import com.good.akkaserver.move.ProtocolDataUnit;
import com.good.akkaserver.msg.PlayerActorCreationMsg;
import com.good.akkaserver.msg.PlayerActorStopMsg;
import com.good.akkaserver.room.ChatClassifier;
import com.good.akkaserver.room.ChatEvent;
import com.good.akkaserver.room.ChatRoom;


/**
 * @author GengQing
 * 2014-3-28
 */
public class AkkaServer  {
	
	private static AkkaServer gameLogicServer;
	private ActorSystem actorSystem;
	
	private ChatRoom  chatRoom;
	
	/** 游戏地图 */
	private GameMap gameMap;

	/**
	 * 所有PlayerActor 的监管者
	 */
	private ActorRef playerMangerActor;
	
	public synchronized static AkkaServer getInstance() {
		if (gameLogicServer == null) {
			gameLogicServer = new AkkaServer();
		}
		return gameLogicServer;
	}
	
	
	private AkkaServer() {	
		
		actorSystem = ActorSystem.create("GameLogicActorSystem");
		playerMangerActor = actorSystem.actorOf(Props.create(PlayerMangerActor.class), "playerActorManger");
		chatRoom = new ChatRoom();
		gameMap = new GameMap();
		
	}
	
	/** 聊天服务  */
	public void publishChat(ChatEvent chatEvent) {
		chatRoom.publish(chatEvent);
	}
	
	/** 订阅聊天信息 */
	public void subChat(ActorRef subscriber, ChatClassifier classifier) {
		chatRoom.subscribe(subscriber, classifier);
	}
	
	public void subMap(ActorRef subscriber, Integer obj) {
		System.out.println("some person subscribe the map " + obj );
		gameMap.subscribe(subscriber, obj);
	}
	
	public void unMap(ActorRef subscriber) {
		gameMap.unsubscribe(subscriber);
	}
	
	public void publishMapEvent(ProtocolDataUnit dataUnit) {
		gameMap.publish(dataUnit);
	}
	
	/** 取消订阅 */
	public void unSubChat(ActorRef ref, ChatClassifier classifier) {
		chatRoom.unsubscribe(ref, classifier);
	}
	
	public ActorSelection lookupActor(int sessionID) {
		return actorSystem.actorSelection("/user/playerActorManger" + Integer.toString(sessionID));
	}
	
	/** 当和客户端建立连接后 ，为客户端准备一个 Fiber 即创建  PlayerActor */
	public void createPlayerActor(PlayerActorCreationMsg msg){
		playerMangerActor.tell(msg, ActorRef.noSender());
	}
	
	/**
	 * 客户端连接断开后，停止对应的 Fiber 即  停止 PlayerActor
	 * @param msg
	 */
	public void stopPlayerActor(PlayerActorStopMsg msg) {
		playerMangerActor.tell(msg, ActorRef.noSender());
	}
	
	/** 分发客户端的请求 给对应的 PlayerActor */
	public void dispatchClientEvent(ClientEvent event) {
		playerMangerActor.tell(event, ActorRef.noSender());
	}
	
	public ActorSystem getActorSystem() {
		return actorSystem;
	}


	public ActorRef getPlayerMangerActor() {
		return playerMangerActor;
	}


}
