package com.jpinfo.mudengine.being.utils;

import com.jpinfo.mudengine.being.model.MudPlayer;
import com.jpinfo.mudengine.common.being.Player;

public class PlayerHelper {

	public static Player buildPlayer(MudPlayer dbPlayer) {
		
		Player response = new Player();
		
		response.setLogin(dbPlayer.getLogin());
		response.setName(dbPlayer.getName());
		response.setPlayerId(dbPlayer.getPlayerId());
		
		
		return response;
	}
}
