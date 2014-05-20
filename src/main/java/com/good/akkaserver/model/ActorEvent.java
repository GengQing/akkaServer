package com.good.akkaserver.model;

/** 
 * 发给playerActor 的事件，请求修改对应的属性
 * @author GengQing
 * 2014-3-28
 */
public final class  ActorEvent {
	
	
	private int attr;
	
	private int value;
	
	/** 增加还是减少 -1: 减少，0：增加 */
	private byte changeType;
	
	public ActorEvent(int type, int value, byte changeType) {
		this.attr = type;
		this.value = value;
		this.changeType = changeType;
	}

	public int getAttr() {
		return attr;
	}

	public void setAttr(int type) {
		this.attr = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public byte getChangeType() {
		return changeType;
	}

	public void setChangeType(byte changeType) {
		this.changeType = changeType;
	}
	
}
