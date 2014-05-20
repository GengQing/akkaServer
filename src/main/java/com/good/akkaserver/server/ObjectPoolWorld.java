package com.good.akkaserver.server;

import org.apache.commons.pool.impl.StackObjectPool;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.good.akkaserver.event.ClientEvent;
import com.good.akkaserver.event.ClientEventFactory;
import com.good.akkaserver.event.ObjectPoolManage;
import com.good.akkaserver.message.MaxSendMessageFactory;
import com.good.akkaserver.message.MidSendMessageFactory;
import com.good.akkaserver.message.MinSendMessageFactory;
import com.good.akkaserver.message.SendMessage;

/**
 * 单例模式 保存一些对象池
 * @author GengQing
 * 2014-4-2
 */
public class ObjectPoolWorld  {

	private static Logger logger = LoggerFactory.getLogger(ObjectPoolWorld.class);
	
	// 消息体类别
	/** 1024 byte */
	public static final int SENDMESSAGE_MIN = 1;// 小型 1024
	
	/** 5120 byte */
	public static final int SENDMESSAGE_MID = 2;// 中型 5120
	
	/** 65000 byte*/
	public static final int SENDMESSAGE_MAX = 3;// 大型 65000
	
	
	// 对象池定义
	private static StackObjectPool clientEventPool = ObjectPoolManage.getPool(
			ClientEventFactory.class, 10000);
	
	private static StackObjectPool maxsendpool = ObjectPoolManage.getPool(
			MaxSendMessageFactory.class, 4000);
	
	private static StackObjectPool midsendpool = ObjectPoolManage.getPool(
			MidSendMessageFactory.class, 10000);
	
	private static StackObjectPool minsendpool = ObjectPoolManage.getPool(
			MinSendMessageFactory.class, 50000);


	
	
	private static ObjectPoolWorld objectPoolWorld = null;// 游戏世界实例
	
	public synchronized static ObjectPoolWorld getInstance() {
		if (objectPoolWorld == null) {
			objectPoolWorld = new ObjectPoolWorld();
		}
		return objectPoolWorld;
	}
	

	private ObjectPoolWorld() {
		logger.info("***************GameWorld 实例化***************");
	}


	/**
	 * 通过对象池申请一个服务器事件对象
	 * @return：服务器事件对象
	 */
	public ClientEvent getClientEvent() {
		synchronized (clientEventPool) {
			try {
				ClientEvent event = (ClientEvent) clientEventPool
						.borrowObject();
				return event;
			} catch (Exception e) {
				logger.error("获取ServerEvent对象出错");
				return new ClientEvent();
			}
		}
	}



	/**
	 * 添加 Function name:returnServerEvent Description: 把服务器事件对象返回给对象池
	 * 
	 * @param event：服务器事件对象
	 */
	public void returnClientEvent(ClientEvent event) {
		synchronized (clientEventPool) {
			try {
				event.reset();
				clientEventPool.returnObject(event);
			} catch (Exception e) {
				logger.error("返回ServerEvent到对象池出错");
			}
		}
	}


	/**
	 * 
	 * Function name:getSendMessage Description: 从对象池获得发送消息体
	 * 
	 * @param level：消息大小级别
	 * @param command：命令码
	 * @param ctx:
	 *            连接实例
	 * @param playerid：账号id
	 * @return：发送消息体
	 */
	public SendMessage getSendMessage(int level, int command,
			ChannelHandlerContext ctx, long playerid) {
		try {
			// 生成时间片
			switch (level) {
			case ObjectPoolWorld.SENDMESSAGE_MIN:
				synchronized (minsendpool) {
					SendMessage sm = (SendMessage) minsendpool.borrowObject();
					sm.setCommand(command);
					sm.setChannelHandlerContent(ctx);
					sm.setPlayerid(playerid);
					return sm;
				}
			case ObjectPoolWorld.SENDMESSAGE_MID:
				synchronized (midsendpool) {
					SendMessage sm = (SendMessage) midsendpool.borrowObject();
					sm.setCommand(command);
					sm.setChannelHandlerContent(ctx);
					sm.setPlayerid(playerid);
					return sm;
				}
			case ObjectPoolWorld.SENDMESSAGE_MAX:
				synchronized (maxsendpool) {
					SendMessage sm = (SendMessage) maxsendpool.borrowObject();
					sm.setCommand(command);
					sm.setChannelHandlerContent(ctx);
					sm.setPlayerid(playerid);
					return sm;
				}

			default:
				synchronized (maxsendpool) {
					SendMessage sm = (SendMessage) maxsendpool.borrowObject();
					sm.setCommand(command);
					sm.setChannelHandlerContent(ctx);
					sm.setPlayerid(playerid);
					return sm;
				}
			}
		} catch (Exception e) {
			logger.error("获取SendMessage对象出错");
			return null;
		}
	}

	/**
	 * 
	 * Function name:getSendMessage Description: 从对象池获得发送消息体
	 * @param ChannelHandlerContext 用户会话
	 * 
	 * @param level：消息大小级别
	 * @param command：命令码
	 * @param playerid:
	 *            帐号ID
	 * @return：发送消息体
	 */
	public SendMessage getSendMessage(int level, int command) {
		try {
			
			switch (level) {
			case ObjectPoolWorld.SENDMESSAGE_MIN:
				synchronized (minsendpool) {
					SendMessage sm = (SendMessage) minsendpool.borrowObject();
					sm.setCommand(command);
					return sm;
				}
			case ObjectPoolWorld.SENDMESSAGE_MID:
				synchronized (midsendpool) {
					SendMessage sm = (SendMessage) midsendpool.borrowObject();
					sm.setCommand(command);
					return sm;
				}
			case ObjectPoolWorld.SENDMESSAGE_MAX:
				synchronized (maxsendpool) {
					SendMessage sm = (SendMessage) maxsendpool.borrowObject();
					sm.setCommand(command);
					return sm;
				}

			default:
				synchronized (maxsendpool) {
					SendMessage sm = (SendMessage) maxsendpool.borrowObject();
					sm.setCommand(command);
					return sm;
				}
			}
		} catch (Exception e) {
			logger.error("获取SendMessage对象出错");
			return null;
		}
	}

	/**
	 * 
	 * Function name:returnSendMessage Description: 返回消息体到对象池
	 * 
	 * @param msg：消息体
	 */
	public void returnSendMessage(SendMessage msg) {
		if (msg == null) {
			return;
		}
		try {
			msg.clean();
			if (msg.getConData().length == MinSendMessageFactory.MAX_LEN) {
				synchronized (minsendpool) {
					minsendpool.returnObject(msg);
				}
			} else if (msg.getConData().length == MidSendMessageFactory.MAX_LEN) {
				synchronized (midsendpool) {
					midsendpool.returnObject(msg);
				}
			} else {
				synchronized (maxsendpool) {
					maxsendpool.returnObject(msg);
				}

			}
		} catch (Exception e) {
			logger.error("返回SendMessage到对象池出错");
		}
	}


	


	
}
