package com.good.akkaserver.room;

public class ChatEvent {
	
	
	public final static byte TO_AREAN = 1;
	
	public final static byte TO_MAP = 2;	
	
	public final static byte TO_COUNTRY = 3;
	
	public final static byte TO_PERSONAL = 4;
	
	private String name;
	
	/** 来自*/
	private int from;
	
	/** 去哪 */
	private int to;
	
	/** 目的类型 */
	private byte type;
	
	/** 聊天内容*/
	private String context;


	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
