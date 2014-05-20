package com.good.akkaserver.util;

import java.util.UUID;

public class CommonUtil {

	
	public static long LONG_MAX_VALUE=(long)Math.pow(2, 63);	
	
	/**
	 * 
	 * Function name:getUUID
	 * Description: 获取全球唯一编号的方法
	 * @return：唯一编号
	 */
	public static long getUUID(){
		UUID uuid = UUID.randomUUID(); 
		return uuid.getLeastSignificantBits()+LONG_MAX_VALUE;
	}
}
