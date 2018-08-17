package com.jpinfo.mudengine.player.model.converter;

import java.util.stream.Collectors;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.player.model.MudPlayer;


public class PlayerConverter {

	private PlayerConverter() { }
	
	public static Player convert(MudPlayer dbPlayer) {
		
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
			
			response.setBeingList(
				dbPlayer.getBeingList().stream()
					.map(PlayerBeingConverter::convert)
					.collect(Collectors.toList())
				);
		}
		
		
		return response;
		
	}
}
