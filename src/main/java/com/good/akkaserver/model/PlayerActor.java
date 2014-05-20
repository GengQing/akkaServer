package com.good.akkaserver.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.good.akkaserver.annoation.Cmd;
import com.good.akkaserver.event.ClientEvent;
import com.good.akkaserver.gamelogic.PlayerRoleInterface;
import com.good.akkaserver.logic.AccountMange;
import com.good.akkaserver.message.SendMessage;
import com.good.akkaserver.move.ProtocolDataUnit;
import com.good.akkaserver.room.ChatClassifier;
import com.good.akkaserver.room.ChatEvent;
import com.good.akkaserver.server.AkkaServer;
import com.good.akkaserver.server.CustomBeanFactory;
import com.good.akkaserver.server.ObjectPoolWorld;
import com.good.akkaserver.server.ProtocolDefine;

/**
 * 对应一个客户端连接 ，每一个客户端连接都会创建此 Fiber。 客户端所以的请求都会在此Fiber 中处理
 * 
 * @author GengQing 2014-4-4
 */
public class PlayerActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private static final AkkaServer  akkaServer = AkkaServer.getInstance();

	private static ObjectPoolWorld objectPoolWorld = ObjectPoolWorld
			.getInstance();

	private static class CommandHandlerHolder {

		public CommandHandlerHolder(BaseEventHandle h, Method m) {
			this.handler = h;
			this.method = m;
		}

		public BaseEventHandle handler;
		public Method method;
	}

	/** 保存 一些公共算法 */
	private static final Map<Integer, CommandHandlerHolder> handlers = new HashMap<Integer, CommandHandlerHolder>();

	static {

		Map<String, BaseEventHandle> map = CustomBeanFactory
				.getBeans(BaseEventHandle.class);
		for (BaseEventHandle handler : map.values()) {

			Class<? extends BaseEventHandle> cls = handler.getClass();

			// 找到所有处理方法
			Method[] methods = cls.getMethods();
			if (methods.length == 0)
				continue;

			CommandHandlerHolder holder = null;

			for (Method method : methods) {
				Cmd cmd = method.getAnnotation(Cmd.class);
				if (cmd == null)
					continue;

				// 把处理方法加入缓存中
				holder = new CommandHandlerHolder(handler, method);
				handlers.put(cmd.value(), holder);
			}
		}

	}

	/** 与客户端的链接 */
	private ChannelHandlerContext ctx;

	/** session ID,每一个客户端连接都会生成一个session ID */
	private int sessionID;
	
	/** 帐号ID */
	private String uid = "";
	

	/** 游戏角色 */
	private PlayerRoleInterface role;

	public PlayerActor(int id) {
		this.setSessionID(id);
	}

	public PlayerActor(int id, ChannelHandlerContext ctx) {
		this.setSessionID(id);
		this.ctx = ctx;
		this.enterInMap(1);
		
		ProtocolDataUnit pd = new ProtocolDataUnit();
		pd.setSessionID(id);
		pd.setDir((byte)6); // 登录
		akkaServer.publishMapEvent(pd);
		
		
	}
	

	@Override
	public void preStart() { // 启动时可以做一些东西

	}
	
	private AccountMange accountMange = (AccountMange)CustomBeanFactory.getBean("accountMange");

	@Override
	public void postStop() {
		log.info("I am going to shutdown!");
		if (uid != "") {
			accountMange.removeAccout(uid);
		}
		akkaServer.unMap(getSelf());
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		if (arg0 instanceof ClientEvent) { 						// 来自客户端的事件
			handleClientEvent((ClientEvent) arg0);
		} else if (arg0 instanceof ChatEvent) {
			sendChat((ChatEvent)arg0);
		} else if (arg0 instanceof ProtocolDataUnit) {          // 地图移动信息
			sendDataUnit((ProtocolDataUnit)arg0);
		}
	}


	/** 处理客户端的请求, */
	private void handleClientEvent(ClientEvent event) {

		try {
			int cmd = event.getCommand();
			CommandHandlerHolder holder = handlers.get(cmd);

			if (holder != null) {
				Object obj = holder.method.invoke(holder.handler, event, this);
				if (obj == null) return;
				if (obj instanceof SendMessage) {
					SendMessage sm = (SendMessage)obj;
					sendToClient(sm);
				} else if (obj instanceof ChatEvent ) {
					
				}
			} else {
				log.error("没有对应的处理方法 ");
			}

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			objectPoolWorld.returnClientEvent(event); // 把对象还给线程池
		}
	}
	
	public void sendChat(ChatEvent event) {
		SendMessage message = objectPoolWorld.getSendMessage(ObjectPoolWorld.SENDMESSAGE_MIN, ProtocolDefine.CHAT);
		message.putString(event.getName());		// 谁
		message.putString(event.getContext());  // 说了什么
		sendToClient(message);
	}
	
	public void sendDataUnit(ProtocolDataUnit dataUnit) {
		SendMessage message = objectPoolWorld.getSendMessage(ObjectPoolWorld.SENDMESSAGE_MIN, ProtocolDefine.MOVE);
		message.putInt(dataUnit.getSessionID());
		message.putByte(dataUnit.getDir());
		message.putInt(dataUnit.getFx());
		message.putInt(dataUnit.getFy());
		long tmp = System.currentTimeMillis();
		int cost = (int)(tmp - dataUnit.getTimestamp());
		message.putInt(cost);					 // 逻辑处理话费时间
		
		sendToClient(message);
		log.info("send move in to client");
	}

	/** 将消息发给客户端 */
	public void sendToClient(SendMessage message) {

		if (message == null) return;
		
		try {
			Channel channel = ctx.getChannel();
			byte[] msgbyte = message.encode();
			ChannelBuffer cb = ChannelBuffers.buffer(msgbyte.length);
			cb.writeBytes(msgbyte);
			channel.write(cb);
		} catch (Exception ex) {
			log.error("下发给客户端失败：{}", ex);
		} finally {
			objectPoolWorld.returnSendMessage(message);
		}
	}
	

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public PlayerRoleInterface getRole() {
		return role;
	}

	public void setRole(PlayerRoleInterface role) {
		
		this.role = role;
		this.subChatEvent(); // 可以听到聊天
//		enterInMap(1);       // 可以看到移动
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	/** 全频道 */
	public void subChatEvent() {
		ChatClassifier classifier = new ChatClassifier();
		classifier.setAreaId(1);
		classifier.setCountryId(1);
		classifier.setMapId(1);
		classifier.setRoleId(this.role.getId());
		akkaServer.subChat(getSelf(), classifier);
	}
	
	public void unSubChatEvent() {
		
	}

	/** 进入地图 */
	public void enterInMap(int mapId) {
		akkaServer.subMap(getSelf(), this.sessionID);
	}



}
