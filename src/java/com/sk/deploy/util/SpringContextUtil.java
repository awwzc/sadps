package com.sk.deploy.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sk.deploy.support.Config;
import com.sk.deploy.support.DeployService;
import com.sk.deploy.support.WarFileListener;
import com.sk.deploy.support.WarFileMonitor;


/**
 * 获取spring上下文
 */
public class SpringContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextUtil.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}

	public static <T> T getBean(Class<T> c) {
		return applicationContext.getBean(c);
	}

	public static WarFileMonitor getWarFileMonitor() {
		return (WarFileMonitor) SpringContextUtil.getBean("warFileMonitor");
	}
	
	public static WarFileListener getWarFileListener() {
		return (WarFileListener) SpringContextUtil.getBean("warFileListener");
	}

	public static DeployService getDeployProvider() {
		return (DeployService)applicationContext.getBean("deployService");
	}
	
	public static Config getConfig() {
	    return (Config)applicationContext.getBean("deployConfig");
    }
	
}