package com.jpinfo.mudengine.player;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.player.model.MudPlayer;

public class PlayerTestData {
	
	private static final String BEING_PREFIX = "src/test/resources/being-";
	private static final String BEING_SUFFIX = ".json";
	
	private static final String MUD_PLAYER_PREFIX = "src/test/resources/mudplayer-";
	private static final String MUD_PLAYER_SUFFIX = ".json";

	private static ObjectMapper jsonMapper = new ObjectMapper();
	
	public static Being loadBeing(Long beingCode) throws IOException{
		
		return jsonMapper.readValue(new File(
				PlayerTestData.BEING_PREFIX +
				beingCode + 
				PlayerTestData.BEING_SUFFIX
				), Being.class);
	}
	

	public static MudPlayer loadMudPlayer(Long playerId) throws IOException{
		
		return jsonMapper.readValue(new File(
				PlayerTestData.MUD_PLAYER_PREFIX +
				playerId + 
				PlayerTestData.MUD_PLAYER_SUFFIX
				), MudPlayer.class);
	}
}
