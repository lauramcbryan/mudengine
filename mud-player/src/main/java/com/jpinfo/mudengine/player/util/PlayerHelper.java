package com.jpinfo.mudengine.player.util;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.PlayerBeing;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.model.MudPlayerBeing;
import com.jpinfo.mudengine.player.model.MudSession;

public class PlayerHelper {

	public static Player buildPlayer(MudPlayer dbPlayer) {
		
		Player response = new Player();
		
		response.setPlayerId(dbPlayer.getPlayerId());
		response.setUsername(dbPlayer.getUsername());
		response.setEmail(dbPlayer.getEmail());
		response.setLocale(dbPlayer.getLocale());
		response.setStatus(dbPlayer.getStatus());
		
		if (dbPlayer.getBeingList()!=null) {
			for(MudPlayerBeing curBeing: dbPlayer.getBeingList()) {
				
				PlayerBeing newBeing = new PlayerBeing();
				newBeing.setBeingCode(curBeing.getId().getBeingCode());
				newBeing.setBeingName(curBeing.getBeingName());
				newBeing.setBeingClass(curBeing.getBeingClass());
				newBeing.setLastPlayed(curBeing.getLastPlayed());
				
				response.getBeingList().add(newBeing);
			}			
		}
		
		
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
		response.setPlayerId(dbSession.getPlayer().getPlayerId());
		response.setClientType(dbSession.getClientType());
		response.setIpAddress(dbSession.getIpAddress());
		
		return response;
	}
}
