package com.sk.deploy.support;

import com.sk.deploy.util.DesUtil;

/**
 * 项目相关信息
 * @author fangyt
 *
 */
public class ProjectInfo {
	
	private String name;
	
	private String ip;
	
	private String user;
	
	private String password;
	
	private String resetCmd;
	
	private String stopCmd;
	
	private String debugCmd;
	
	private String serverPort;
	
	private String dataPath;
	
	private String warPath;

	public String getName() {
    	return name;
    }

	public void setName(String name) {
    	this.name = name;
    }

	public String getIp() {
    	return ip;
    }

	public void setIp(String ip) {
    	this.ip = ip;
    }

	public String getUser() {
    	return user;
    }

	public void setUser(String user) {
    	this.user = user;
    }

	public String getPassword() {
    	try {
	        return new DesUtil().decrypt(this.password);
        } catch (Exception e) {
        	System.out.println("解密错误");
	        e.printStackTrace();
	        return null;
        }
    }

	public void setPassword(String password) {
    	this.password = password;
    }

	public String getResetCmd() {
    	return resetCmd;
    }

	public void setResetCmd(String resetCmd) {
    	this.resetCmd = resetCmd;
    }

	public String getStopCmd() {
    	return stopCmd;
    }

	public void setStopCmd(String stopCmd) {
    	this.stopCmd = stopCmd;
    }

	public String getDebugCmd() {
    	return debugCmd;
    }

	public void setDebugCmd(String debugCmd) {
    	this.debugCmd = debugCmd;
    }

	public String getServerPort() {
    	return serverPort;
    }

	public void setServerPort(String serverPort) {
    	this.serverPort = serverPort;
    }

	public String getDataPath() {
    	return dataPath;
    }

	public void setDataPath(String dataPath) {
    	this.dataPath = dataPath;
    }

	public String getWarPath() {
    	return warPath;
    }

	public void setWarPath(String warPath) {
    	this.warPath = warPath;
    }
	
}
