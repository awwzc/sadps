package com.sk.deploy.web;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import javax.servlet.http.HttpSession;

public class HttpSessionConfigurator extends ServerEndpointConfig.Configurator  {
	
	@Override
	public void modifyHandshake(ServerEndpointConfig config,HandshakeRequest request, HandshakeResponse response) {
		System.out.println("modifyHandshake....");
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		config.getUserProperties().put(HttpSession.class.getName(), httpSession);
	}
	
}
