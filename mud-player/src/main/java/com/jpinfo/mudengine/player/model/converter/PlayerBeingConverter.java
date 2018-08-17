package com.jpinfo.mudengine.player.model.converter;

import com.jpinfo.mudengine.common.player.PlayerBeing;
import com.jpinfo.mudengine.player.model.MudPlayerBeing;

public class PlayerBeingConverter {

	private PlayerBeingConverter() { }
	
	public static PlayerBeing convert(MudPlayerBeing dbPlayerBeing) {
	
		PlayerBeing newBeing = new PlayerBeing();
		
		newBeing.setBeingCode(dbPlayerBeing.getId().getBeingCode());
		newBeing.setBeingName(dbPlayerBeing.getBeingName());
		newBeing.setBeingClass(dbPlayerBeing.getBeingClass());
		newBeing.setLastPlayed(dbPlayerBeing.getLastPlayed());
		
		return newBeing;
	}
}
