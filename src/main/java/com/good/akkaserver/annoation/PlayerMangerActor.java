package com.good.akkaserver.annoation;

import org.jboss.netty.channel.ChannelHandlerContext;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.good.akkaserver.event.ClientEvent;
import com.good.akkaserver.model.ActorEvent;
import com.good.akkaserver.model.PlayerActor;
import com.good.akkaserver.msg.PlayerActorCreationMsg;
import com.good.akkaserver.msg.PlayerActorStopMsg;
import com.good.akkaserver.room.ChatClassifier;
import com.good.akkaserver.server.AkkaServer;

/**
 * 
 * 所有PlayerActor 的 父亲 ,负责创建和关闭 PlayerActor，转发客户端请求 给对应的  PlayerActor
 * @author GengQing
 * 2014-3-28
 */
public class PlayerMangerActor extends UntypedActor {
	

	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object arg0) throws Exception {
		
		if (arg0 instanceof PlayerActorCreationMsg) { // 客户端连接，创建一个Fiber
			createPlayerActorRef((PlayerActorCreationMsg)arg0);
		} 
		else if (arg0 instanceof ClientEvent) {  	  // 客户端请求
			ClientEvent event = (ClientEvent)arg0;
			dispatchEvent(event);
		}
		else if (arg0 instanceof PlayerActorStopMsg) { // 客户端连接断开后，关闭 对应的Fiber
			stopPlayerActor((PlayerActorStopMsg)arg0);
		} 
	}
	
	/**
	 * 创建一对应的ActorRef, 名字就是 SESSIONID
	 * @param create
	 */
	private void createPlayerActorRef(PlayerActorCreationMsg create) {
		int id = create.getSessionID();
		ChannelHandlerContext context = create.getCtx();
		ActorRef  ref = getContext().actorOf(Props.create(PlayerActor.class, id, context),Integer.toString(id));
//		getContext().system().eventStream().subscribe(ref, ActorEvent.class);
		
//		getContext().system().eventStream().publish(event)

		log.info("the children num is {} " , this.getChildNum());
	}
	
	/** 把客户端请求 发给对应的  PlayerActor */
	private void dispatchEvent(ClientEvent event) {
		ActorRef child =  getContext().getChild(Integer.toString(event.getSessionID()));
		if (child != null) {
			child.tell(event, getSelf());
		} else {
			log.error("the PlayerActorRef is crashed, sessionID : {} " , event.getSessionID());
		}
	}
	
	/**
	 * 通过角色ID查找玩家角色
	 * @param actorId
	 * @return 如果不存在返回NULL
	 */
	public ActorRef lookupActor(int sessionID) {
		ActorRef child =  getContext().getChild(Integer.toString(sessionID));
		if (child == null) {
			log.info("查找的PlayerActor 不存在 ：" + sessionID);
		}
		return child;
	}
	
	/**
	 * 关闭一个PlayerActor
	 * @param sessionID
	 */
	public void stopPlayerActor(PlayerActorStopMsg msg) {
		
		int sessionID = msg.getSessionID();
		ActorRef actorRef = this.lookupActor(sessionID);
		if (actorRef != null ) {
			getContext().stop(actorRef);
			// 发送一颗毒药给 actor, 让其执行完其他的邮件
			actorRef.tell(akka.actor.PoisonPill.getInstance(), getSelf());
			log.info("the client exit the game");
		} else {
			log.info(" fiber is not exist id: {}" , sessionID);
		}
	}
	
	public int getChildNum() {
		return getContext().children().size();
	}
	
	
//	/** 创建角色的事件，需要发给客户端  */
//	private  void createActor(ServerEvent serverEvent) {
//		
//		int id = PlayerRoleID.getAndIncrement();
//		String name = serverEvent.getString();
//		ActorRef actorRef = createActor(id, name, serverEvent.getChannelHandlerContent());
//		actorRef.tell(serverEvent, getSelf());
		
		
//		PlayerSession playerSession = new PlayerSession();
//		playerSession.setRoleId(id);
//		
//		serverEvent.getChannelHandlerContent().setAttachment(playerSession);
//		
//		
//		ContextMessageDown contextMessageDown = new ContextMessageDown();
//		
//		
//		contextMessageDown.ctx = serverEvent.getChannelHandlerContent();
//		
//		contextMessageDown.serverEvent = serverEvent;
//		
//		// 用谷歌protolbuff 
//		com.good.akkaserver.protolbuf.LoginProto.MsgInfo.Builder builder =
//				com.good.akkaserver.protolbuf.LoginProto.MsgInfo.newBuilder();
//		builder.setGoodID(100);
//		builder.setGuid("1111-1111-3333");
//		builder.setOrder(8);
//		builder.setType("Item");
//		builder.setID(10);
//		builder.setUrl("http://www.hao123.com");
//		com.good.akkaserver.protolbuf.LoginProto.MsgInfo info = builder.build();
//		contextMessageDown.ml = info;
		
//		akkaServer.getGameWorld().tell(contextMessageDown, getSelf());
		
		
//	}

}
