package com.good.akkaserver.server;

/**
 * 
 * @author 
 * Class name:ProtocolDefine
 * Description:用于定义游戏服务和客户端之间的协议，如交互指令等
 * Create time:May 4, 2009
 */

/**
 * 协议指令分区
 */


public interface ProtocolDefine {
	
	/** 帐号注册 */
	public static final int ACCOUNT_REG = 1;
	
	/** 帐号登录 */
	public static final int ACCOUNT_LOGIN = 2;

	/** 创建玩家角色 */
	public final static int CREATE_PLAYER_ROLE = 3;
	
	/** 时间同步*/
	public final static int TIME_SYNC = 4;
	
	
	/** 聊天 */
	public final static int CHAT = 100;
	
	/** 连接广播 */
	public final static int CONNECTED = 103;
	
	/** 移动 */
	public final static int MOVE = 101;
	
	
	
	
}
