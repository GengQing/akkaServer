package com.good.akkaserver.account;

import java.io.Serializable;

/**
 * 用户信息
 * @author GengQing
 * 2014-4-10
 */
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 307729280667898927L;
	
	/** 帐号 */
	private String uid;
	
	/** 密码 */
	private String password;
	
	/** */
	private String address;
	
	
	public User(String uid, String password) {
		this.uid = uid;
		this.password = password;
	}
	public User() {
	}
	
	

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
