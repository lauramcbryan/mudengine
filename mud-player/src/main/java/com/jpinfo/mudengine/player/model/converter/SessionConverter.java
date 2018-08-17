package com.jpinfo.mudengine.player.model.converter;

import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.player.model.MudSession;

public class SessionConverter {

	private SessionConverter() { }
	
	public static Session convert(MudSession dbSession) {
		
		Session response = new Session();
		
		response.setSessionId(dbSession.getSessionId());
		response.setSessionStart(dbSession.getSessionStart());
		response.setSessionEnd(dbSession.getSessionEnd());
		response.setPlayerId(dbSession.getPlayer().getPlayerId());
		response.setClientType(dbSession.getClientType());
		response.setIpAddress(dbSession.getIpAddress());
		response.setBeingCode(dbSession.getBeingCode());
		
		return response;
	}
}
