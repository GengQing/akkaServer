package com.good.akkaserver.model.actorevent;

/** 赠送金额  */
public class PresentMoney {
	
	/** 总额 */
	private int amount;
	
	private int from ;

	/** 赠送者 */
	private int sessionID;
	
	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}
	
}
