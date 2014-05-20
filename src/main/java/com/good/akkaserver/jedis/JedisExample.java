package com.good.akkaserver.jedis;

import java.util.ResourceBundle;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisExample {
	
	private static JedisPool pool; 
	
	public static void test() {
		Jedis jedis = new Jedis("localhost");
		String keys = "name";
		jedis.del(keys);
		jedis.set(keys, "snowolf");
		String value = jedis.get(keys);
		System.out.println(value);  
	}
	static {
		ResourceBundle bundle = ResourceBundle.getBundle("jedis");
		if (bundle == null) {
	        throw new IllegalArgumentException(  
	                "[redis.properties] is not found!"); 
		}
		
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(Integer.valueOf(bundle  
	            .getString("redis.pool.maxActive")));
		
	    config.setMaxIdle(Integer.valueOf(bundle  
	            .getString("redis.pool.maxIdle")));
	    
	    config.setMaxWaitMillis(Long.valueOf(bundle.getString("redis.pool.maxWait")));  
	  
	    config.setTestOnBorrow(Boolean.valueOf(bundle  
	            .getString("redis.pool.testOnBorrow")));  
	  
	    config.setTestOnReturn(Boolean.valueOf(bundle  
	            .getString("redis.pool.testOnReturn"))); 
	    
	  
	    pool = new JedisPool(config, bundle.getString("redis.ip"), Integer.valueOf(bundle.getString("redis.port")));
	    
	}
	public static void main(String[] args) {
//		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		Jedis jedis = pool.getResource();
		
		try {
			jedis.set("foo", "bar");
			String foobar = jedis.get("foo");
			System.out.println(foobar);
			jedis.zadd("sose", 0,"car");
			jedis.zadd("sose", 0, "bike");
			Set<String> sose = jedis.zrange("sose", 0, -1);
			
		} finally { // 释放对象池 
			pool.returnResource(jedis);
		}
		pool.destroy();
	}

}
