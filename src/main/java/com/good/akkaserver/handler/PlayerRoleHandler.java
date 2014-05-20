package com.good.akkaserver.handler;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.good.akkaserver.account.User;
import com.good.akkaserver.annoation.Cmd;
import com.good.akkaserver.event.ClientEvent;
import com.good.akkaserver.gamelogic.PlayerRole;
import com.good.akkaserver.gamelogic.PlayerRoleInterface;
import com.good.akkaserver.logic.AccountMange;
import com.good.akkaserver.message.SendMessage;
import com.good.akkaserver.model.BaseEventHandle;
import com.good.akkaserver.model.PlayerActor;
import com.good.akkaserver.move.ProtocolDataUnit;
import com.good.akkaserver.room.ChatEvent;
import com.good.akkaserver.server.AkkaServer;
import com.good.akkaserver.server.ObjectPoolWorld;
import com.good.akkaserver.server.ProtocolDefine;

@Component
public class PlayerRoleHandler extends BaseEventHandle implements ProtocolDefine {

	
	private static ObjectPoolWorld objectPool = ObjectPoolWorld.getInstance();
	
	private static final AkkaServer  akkaServer = AkkaServer.getInstance();
	
	public static AtomicInteger i = new AtomicInteger(0);
	
	@Autowired
	private AccountMange accountMange;
	
	/** 注册帐号 */
	@Cmd(ACCOUNT_REG)
	public SendMessage registerAccount(ClientEvent event, PlayerActor pa) {
		
		logger.debug(" === somebody try to register account");
		SendMessage message = objectPool.getSendMessage(ObjectPoolWorld.SENDMESSAGE_MIN, event.getCommand());
		String uname = event.getString();
		String pw = event.getString();
		User user = accountMange.createUser(uname, pw);
		if (user != null) {
			pa.setUid(user.getUid());
			message.putBoolean(true);
		} else {
			message.putBoolean(false);
		}
		return message;
	}
	
	/** 登录验证 */
	@Cmd(ACCOUNT_LOGIN) 
	public SendMessage checkAccount(ClientEvent event, PlayerActor pa) {
		
		SendMessage message = objectPool.getSendMessage(ObjectPoolWorld.SENDMESSAGE_MIN, event.getCommand());
		String uname = event.getString();
		String pw = event.getString();
		boolean ispass = accountMange.userCheck(uname, pw);
		if (ispass) {
			pa.setUid(uname);
		}
		message.putBoolean(ispass);
		logger.info("有登录 username = {}, password = {}", uname, pw);
		return message;
	}
	
	/** 创建玩家角色 */
	@Cmd(CREATE_PLAYER_ROLE)
	public SendMessage createPlayerRole(ClientEvent event, PlayerActor pa) {
		
		SendMessage message = objectPool.getSendMessage(ObjectPoolWorld.SENDMESSAGE_MIN, event.getCommand());
		if (pa.getUid() != "") {
			PlayerRole playerRole = new PlayerRole();
			String name = event.getString();
			logger.info("创建角色 name = {}", name);
			playerRole.setName(name);
			playerRole.setId(i.getAndIncrement());
			pa.setRole(playerRole);
			
			message.putBoolean(true);
		} else {
			message.putBoolean(false); 	// 未登录
			logger.error(" === you are not login yet");
		}
		
		return message;
	}
	
	
	/** 聊天需要广播 */
	@Cmd(CHAT)
	public ChatEvent sendChat(ClientEvent event, PlayerActor pa) {
		
		PlayerRoleInterface pr = pa.getRole();
		String context = event.getString();
		byte type = event.getByte();
		int to = event.getInt();
		ChatEvent cevent = new ChatEvent();
		
		cevent.setType(type);
		cevent.setTo(to);
		cevent.setFrom(pr.getId());
		cevent.setContext(context);
		cevent.setName(pr.getName());
		
		akkaServer.publishChat(cevent);
		
		return cevent;
	}
	
	@Cmd(MOVE)
	public ProtocolDataUnit moveOnMap(ClientEvent event, PlayerActor pa) {
		
		PlayerRoleInterface pr = pa.getRole();
		byte dir = event.getByte();
		int fx = event.getInt();
		int fy = event.getInt();
		
		int speed = event.getInt();
	
		ProtocolDataUnit dataUnit = new ProtocolDataUnit();
		//dataUnit.setId(pr.getId());
		dataUnit.setSpeed(speed);
		dataUnit.setDir(dir);
		dataUnit.setFx(fx);
		dataUnit.setFy(fy);
		dataUnit.setSessionID(pa.getSessionID());
		dataUnit.setTimestamp(event.getTimestamp());
		akkaServer.publishMapEvent(dataUnit);
		logger.debug(" I am moving");
		return null;
		
	}
	
	
	
}
