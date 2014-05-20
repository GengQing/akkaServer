package com.good.akkaserver.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 黄骏
 * File name:UserSession.java
 * Description:玩家会话类，用于存放玩家此次会话信息，通用socket和http
 * Create time:2011-1-6
 */
public class UserSession {
	
	
	public final static long TIME_OUT = 5*60*1000;				// 5分钟timeout
	public final static int DEFAULT_CLIENT_VERSION = 200200;	// 默认客户端版本号
	
	private long playerid;				//	账号id
	private int actorid;				//	登陆的角色id
	private int connectType;			//	会话连接类型
	private int clientVersion = DEFAULT_CLIENT_VERSION;//客户端版本号
	private ChannelHandlerContext ctx;  //  会话实例，主要用于socket
	private long lastSessionUpdate;	  	//  会话最后一次更新时间
	private int lang;
	
	public UserSession(long _playerid, int _connectType){
		playerid = _playerid;
		connectType = _connectType;
	}
	
	
	
	//属性get和set
	public long getPlayerid() {
		return playerid;
	}

	public void setPlayerid(long playerid) {
		this.playerid = playerid;
	}


	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public long getLastSessionUpdate() {
		return lastSessionUpdate;
	}

	public void setLastSessionUpdate(long lastSessionUpdate) {
		this.lastSessionUpdate = lastSessionUpdate;
	}

	public int getConnectType() {
		return connectType;
	}

	public void setConnectType(int connectType) {
		this.connectType = connectType;
	}

	public int getLang() {
		return lang;
	}

	public void setLang(int lang) {
		this.lang = lang;
	}

	public int getActorid() {
		return actorid;
	}

	public void setActorid(int actorid) {
		this.actorid = actorid;
	}


	public int getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(int clientVersion) {
		this.clientVersion = clientVersion;
	}

	
}
