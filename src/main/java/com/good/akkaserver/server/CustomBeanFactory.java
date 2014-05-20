package com.good.akkaserver.server;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * Class name:CustomBeanFactory
 * Description:用于通过Spring进行实例管理的方法，提供根据BeanName获取Bean实例
 */
@Configuration
public class CustomBeanFactory implements ApplicationContextAware {
	
	private static ApplicationContext context = null;

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	public static <T> Map<String, T> getBeans(Class<T> clazz) {
		return context.getBeansOfType(clazz);
	}

	public static void main(String[] args) {

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		CustomBeanFactory.context = applicationContext;
	}
}
