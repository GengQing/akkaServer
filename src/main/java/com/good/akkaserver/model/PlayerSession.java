package com.good.akkaserver.model;


/**
 * 
 * @author GengQing
 * 2014-4-4
 */
public class PlayerSession {
	
	
	private int id;
	
	/** 帐号	*/
	private long pid;
	
	/** 角色ID*/
	private int roleId;

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
