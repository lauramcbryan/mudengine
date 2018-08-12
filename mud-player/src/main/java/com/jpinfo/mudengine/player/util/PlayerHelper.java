package com.jpinfo.mudengine.player.util;

import java.util.Random;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.PlayerBeing;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.model.MudPlayerBeing;
import com.jpinfo.mudengine.player.model.MudSession;

public class PlayerHelper {
	
	private static final int FIRST_PWD_CHAR = 48;  // '0'
	private static final int LAST_PWD_CHAR = 122;  // 'z'
	
	private PlayerHelper() {}

	public static Player buildPlayer(MudPlayer dbPlayer) {
		
		Player response = new Player();
		
		response.setPlayerId(dbPlayer.getPlayerId());
		response.setUsername(dbPlayer.getUsername());
		response.setEmail(dbPlayer.getEmail());
		response.setLocale(dbPlayer.getLocale());
		response.setStatus(dbPlayer.getStatus());
		
		
		
		switch(dbPlayer.getStatus()) {
			case Player.STATUS_ACTIVE:
				response.setStrStatus(LocalizedMessages.getMessage(LocalizedMessages.PLAYER_ACTIVE_STATUS));
				break;				
			case Player.STATUS_BANNED:
				response.setStrStatus(LocalizedMessages.getMessage(LocalizedMessages.PLAYER_BANNED_STATUS));
				break;				
			case Player.STATUS_BLOCKED:
				response.setStrStatus(LocalizedMessages.getMessage(LocalizedMessages.PLAYER_BLOCKED_STATUS));
				break;				
			case Player.STATUS_INACTIVE:
				response.setStrStatus(LocalizedMessages.getMessage(LocalizedMessages.PLAYER_INACTIVE_STATUS));
				break;				
			case Player.STATUS_PENDING:
				response.setStrStatus(LocalizedMessages.getMessage(LocalizedMessages.PLAYER_PENDING_STATUS));
				break;
			default: 
				response.setStrStatus(LocalizedMessages.getMessage(LocalizedMessages.PLAYER_UNKNOWN_STATUS));
		}
		
		
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
		
		Random rand = new Random(System.currentTimeMillis());
		
		StringBuilder hash = new StringBuilder(8);
		
		for(int k=0;k<8;k++)
			hash.append((char)rand.nextInt(LAST_PWD_CHAR - FIRST_PWD_CHAR));
		
		return hash.toString();
	}
	
	public static Session buildSession(MudSession dbSession) {
		
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
