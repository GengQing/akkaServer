package com.good.akkaserver.dao;

/**
 * 对象DAO 的基础类，保存一些通用的方法,建议使用hashSet 而不是get 和 set 的方式，节约内存
 * @author GengQing
 * 2014-4-11
 * @param <T>
 */
public interface BaseDao<T> {
	
	/** 保存或者更新对象 */
	public void saveOrUpdate(String key, T value);
	
	/** 查找对象*/
	public T get(String key);
	
	/** 删除对象 */
	public void delete(String key);

}
