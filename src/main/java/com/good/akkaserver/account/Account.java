package com.good.akkaserver.account;

import java.io.Serializable;

/**
 * 
 * @author GengQing
 * 2014-4-10
 */
public class Account implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3994268144725194568L;
	
	private long id;
	
	private String name;
	
	private String password;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
