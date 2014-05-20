package com.good.akkaserver.dao;

import javax.annotation.Resource;

import org.springframework.data.redis.core.ValueOperations;

public class BaseDaoImpl<T> implements BaseDao<T> {
	
	@Resource(name="redisTemplate")
	private ValueOperations<String, T> objOpers;

	@Override
	public void saveOrUpdate(String key, T value) {
		objOpers.set(key, value);
	}

	@Override
	public T get(String key) {
		return objOpers.get(key);
	}

	@Override
	public void delete(String key) {
		objOpers.getOperations().delete(key);
	}


}
