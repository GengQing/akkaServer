package com.good.akkaserver.logic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.good.akkaserver.account.User;
import com.good.akkaserver.dao.UserDao;

@Component
public class AccountMange {
	
	private static Logger logger = LoggerFactory.getLogger(AccountMange.class);

	
	@Autowired
	private UserDao userDao;
	
	/** 在线玩家 <uid>*/
	private static Set<String> accounts = Collections.synchronizedSet(new HashSet<String>());

	/** 移除在线玩家 */
	public void removeAccout(String uid) {
		accounts.remove(uid);
	}
	
	/** 创建账户 */
	public User createUser(String uid, String pwd) {
		
		User user = userDao.get("user:" +uid);
		if (user == null) {
			user = new User(uid, pwd);
			userDao.saveOrUpdate("user:"+uid, user);
			accounts.add(uid);
			return user;
		} else {
			logger.debug("the account uid is exists");
		}
		
		return null;
	}
	
	/** 用户登录验证*/
	public boolean userCheck(String uid, String pwd) {
		
		if (accounts.contains(uid)) {
			logger.error("another person already login with this account");
			return false;
		}
		User user = userDao.get("user:" +uid);
		if (user == null) {
			return false;
		}
		return true;
	}
	

}
