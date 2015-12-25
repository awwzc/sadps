package com.sk.deploy.web;

public interface Messager {
    void sendMessage(String msg);
    
    void sendWarnMessage(String msg);
    
	void sendErrorMessage(String msg);
	
}
