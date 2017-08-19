package com.jpinfo.mudengine.player.util;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.model.MudSession;

public class PlayerHelper {

	public static Player buildPlayer(MudPlayer dbPlayer) {
		
		Player response = new Player();
		
		response.setPlayerId(dbPlayer.getPlayerId());
		response.setUsername(dbPlayer.getUsername());
		response.setName(dbPlayer.getName());
		response.setEmail(dbPlayer.getEmail());
		response.setLanguage(dbPlayer.getLanguage());
		response.setCountry(dbPlayer.getCountry());
		response.setStatus(dbPlayer.getStatus());
		
		return response;
	}
	
	public static String generatePassword() {
		
		// TODO: Do something real here
		return "dummy";
	}
	
	public static Session buildSession(MudSession dbSession) {
		
		Session response = new Session();
		
		response.setSessionId(dbSession.getSessionId());
		response.setSessionStart(dbSession.getSessionStart());
		response.setSessionEnd(dbSession.getSessionEnd());
		response.setLanguage(dbSession.getPlayer().getLanguage());
		response.setCountry(dbSession.getPlayer().getCountry());
		response.setPlayerId(dbSession.getPlayer().getPlayerId());
		
		return response;
	}
}
