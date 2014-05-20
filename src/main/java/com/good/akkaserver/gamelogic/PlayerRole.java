package com.good.akkaserver.gamelogic;

/**
 * 玩家游戏中的角色
 * @author GengQing
 * 2014-4-4
 */
public class PlayerRole implements PlayerRoleInterface {

	/** 角色ID*/
	private int id;
	
	/** 角色名 */
	private String name;
	

	/* (non-Javadoc)
	 * @see com.good.akkaserver.gamelogic.PlayerRoleInterface#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.good.akkaserver.gamelogic.PlayerRoleInterface#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	
	
}
