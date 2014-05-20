package com.good.akkaserver.move;

/**
 * 协议数据包
 * @author GengQing
 * 2014-4-15
 */
public class ProtocolDataUnit {
	
	/** 角色ID */
	private int id;
	
	/** 横坐标     */
	private int x;
	
	/** 纵坐标      */
	private int y;
	
	private int fx;
	
	private int fy;
	
	/** 移动速度  */
	private int speed;
	
	/** type = 1 :表示离开地图*/
	private byte type;
	
	private byte dir ;
	
	/** 时间戳 */
	private long timestamp;
	
	private int sessionID;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}



	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public int getFx() {
		return fx;
	}

	public void setFx(int fx) {
		this.fx = fx;
	}

	public int getFy() {
		return fy;
	}

	public void setFy(int fy) {
		this.fy = fy;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getDir() {
		return dir;
	}

	public void setDir(byte dir) {
		this.dir = dir;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	

}
