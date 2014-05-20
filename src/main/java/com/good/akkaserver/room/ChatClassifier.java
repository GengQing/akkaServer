package com.good.akkaserver.room;

/**
 * 
 * @author GengQing
 * 2014-4-14
 */
public class ChatClassifier {

	
	/** 订阅者地图ID， 用于同地图聊天*/
	private int mapId;
	
	/** 订阅者区ID， 用于同分区聊天，可以是世界聊天*/
	private int areaId;
	
	/** 订阅者国家ID， 用于同国家聊天*/
	private int countryId;
	
	
	/** 订阅者角色ID,  用于私聊*/
	private int roleId;

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}


	public int compareTo(ChatClassifier arg1) {
		return 0;
	}
	
}
