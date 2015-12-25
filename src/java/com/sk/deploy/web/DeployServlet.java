package com.sk.deploy.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sk.deploy.support.Config;
import com.sk.deploy.support.DeployService;
import com.sk.deploy.support.LogLevel;
import com.sk.deploy.support.WarFileListener;
import com.sk.deploy.support.WarFileMonitor;
import com.sk.deploy.util.DeployHelper;
import com.sk.deploy.util.SpringContextUtil;

@ServerEndpoint(value = "/deploy",configurator=HttpSessionConfigurator.class)
public class DeployServlet implements Messager {

	public Session session;

	@OnOpen
	public void onOpen(Session session,EndpointConfig config){
		this.session = session;
    	WarFileMonitor warFileMonitor = SpringContextUtil.getWarFileMonitor();
		WarFileListener warFileListener = SpringContextUtil.getWarFileListener();
		warFileListener.setMessager(this);
		warFileMonitor.monitor(Config.MONITOR_PATH, warFileListener);
	    try {
	    	if (warFileMonitor.isRunning()) {
	    		warFileMonitor.stop();
	    		sendMessage("restart directory monitor...");
            }else{
            	sendMessage("start directory monitor...");
            }
	    	warFileMonitor.start();
        } catch (Exception e) {
            sendErrorMessage("start directory monitor error" + e.getMessage());
	        e.printStackTrace();
        }
	}

	@OnClose
	public void onClose(){
	}
	
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		JSONObject jsonObject = JSON.parseObject(message);
		
		//用户名用于记录用户操作日志
		String userName = jsonObject.getString("userName"); 
		String command = jsonObject.getString("command");   
		String projects = jsonObject.getString("projects");
		if(command.isEmpty()){
			sendErrorMessage("invalide command!");
			return;
		}
		DeployService deployService = SpringContextUtil.getDeployProvider();
		deployService.setUserName(userName);
		deployService.setMessager(this);
		if("listWars".equals(command)){
			deployService.listWars();
		}else if("uploadAndRestart".equals(command)){
			try {
				deployService.checkWarAndUpload();
			} catch (Exception e1) {
				sendErrorMessage("uploadAndRestart exception：" + e1.getMessage());
			}
		}else if("deployAndRestart".equals(command)){
			if(StringUtils.isNotEmpty(projects)){
				try {
	                deployService.build(projects);
                } catch (Exception e) {
	                sendErrorMessage("deployAndRestart exception：" + e.getMessage());
                }
			}
		}
		else if("restart".equals(command)){
		    if(StringUtils.isNotEmpty(projects)){
		        try {
		            deployService.restart(projects);
		        } catch (Exception e) {
		            sendErrorMessage("restart exception：" + e.getMessage());
		        }
		    }
		}
		else if("restartWithDebugModel".equals(command)){
			if(StringUtils.isNotEmpty(projects)){
				try {
					deployService.stopAndDebugCmd(projects);
				} catch (Exception e) {
					sendErrorMessage("restartWithDebugModel exception：" + e.getMessage());
				}
			}
			
		}
		else if("viewDiskUseAge".equals(command)){
			if(StringUtils.isNotEmpty(projects)){
				try {
					deployService.viewDiskUseAge(projects);
				} catch (Exception e) {
					sendErrorMessage("viewDiskUseAge exception：" + e.getMessage());
				}
			}
			
		}else if("addConfig".equals(command)){
            if(StringUtils.isNotEmpty(projects)){
                String config = jsonObject.getString("config");
                try {
                    deployService.addConfig(projects.split(":")[0], config);
                } catch (Exception e) {
                    sendErrorMessage("addConfig exception：" + e.getMessage());
                }
            }
            
        }else if("modifyConfig".equals(command)){
            if(StringUtils.isNotEmpty(projects)){
                String config = jsonObject.getString("config");
                try {
                    deployService.modifyConfig(projects.split(":")[0], config);
                } catch (Exception e) {
                    sendErrorMessage("modifyConfig exception：" + e.getMessage());
                }
            }
        }
	}
	
	public  void sendMessage(String message) {
		try {
	        this.session.getBasicRemote().sendText(parseMessage(message,LogLevel.INFO));
        } catch (IOException e) {
	        e.printStackTrace();
        }
	}
	
	public void sendWarnMessage(String message){
	    try {
	        this.session.getBasicRemote().sendText(parseMessage(message,LogLevel.WARN));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void sendErrorMessage(String message){
	    try {
	        this.session.getBasicRemote().sendText(parseMessage(message,LogLevel.ERROR));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private String parseMessage(String message,LogLevel logLevel){
	    Map<String,String> resultMap = new HashMap<String,String>();
        String fullMessage = null;
        switch (logLevel) {
        case INFO:
            fullMessage = DeployHelper.getTimestamp() + message;
            break;
        case WARN:
            fullMessage = String.format(LogLevel.WARN.getStyle(),DeployHelper.getTimestamp() + message);
            break;
        case ERROR:
            fullMessage = String.format(LogLevel.ERROR.getStyle(),DeployHelper.getTimestamp() + message);
            break;
        }
        resultMap.put("message", fullMessage);
        return JSON.toJSONString(resultMap);
	}
	
	@OnError
	public void onError(Session session, Throwable error){
		String errorMsg = error.getMessage();
		if(errorMsg.indexOf("aborted the connection") > -1){
			System.out.println("客户端主动中断了连接。");
		}else{
			System.out.println(errorMsg);
		}
	}

}