package com.good.akkaserver.jedis;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import com.good.akkaserver.account.User;

@Component
public class SpringExample {

	public static ApplicationContext app;  
	
	static {
		 app = new ClassPathXmlApplicationContext("bean.xml");  
	}
	
	@Autowired
	private RedisTemplate<String, String> template;
	
	
	@Resource(name="redisTemplate")
	private ListOperations<String, String> listOps;
	
	public void addLink(String userId, URL url) {
		listOps.leftPush(userId, url.toExternalForm());
//		template.boundListOps(userId).leftPush(url.toExternalForm());
	}
	
	public void getLink(String userId) {
		List<String> st = listOps.range(userId, 0, 10);
		System.out.println(st);
	}
	
	@Resource(name="redisTemplate")
	private ValueOperations<String, User> userOper;
	
	public void saveUser(User user) {
		userOper.set("user:"+user.getUid(), user);
		System.out.println(userOper.get("user:"+user.getUid()).getAddress());
	}
	
	public void save(final String userId, final URL url) {
		template.execute(new RedisCallback<Object>() {

			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] key = template.getStringSerializer().serialize(userId);
				byte[] value = template.getStringSerializer().serialize(url.toExternalForm());
				connection.set(key, value);
				return (Object)null;
			}
			
		});
	}
	
	public void delete(final String userId) {
		template.execute(new RedisCallback<Object>() {  
	        public Object doInRedis(RedisConnection connection) {  
	            connection.del(template.getStringSerializer().serialize(userId));  
	            return null;  
	        }  
	    }); 
	}
	public URL read(final String userId) {
		
		return template.execute(new RedisCallback<URL>() {

			@Override
			public URL doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] key = template.getStringSerializer().serialize(userId);
				if (connection.exists(key)) {
					 byte[] value = connection.get(key);
					 
//					 RedisSerializer<String>  ser =  (RedisSerializer<String>) template.getDefaultSerializer();
//					 ser.serialize(userId);
					 String url = template.getStringSerializer().deserialize(value);
					 try {
						URL url2 = new URL(url);
						return url2;
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			}  
		});
	}
	
	public static void main(String[] args) {
		SpringExample se = (SpringExample)app.getBean("springExample");
		try {
			URL url = new URL("http://www.baidu.com");
			se.addLink("baidu", url);
			se.getLink("baidu");
			User user = new User();
			user.setAddress("guangdong");
			user.setUid("1");
			se.saveUser(user);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
