package com.jpinfo.mudengine.player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.model.MudSession;

public class PlayerTestData {
	
	public static final LocalDateTime REFERENCE_DATE = LocalDateTime.now();
	
	public static final Long TEST_BEING_CODE = 1L;
	public static final Long TEST_PLAYER_ID = 1L;
	public static final Long TEST_PENDING_PLAYER_ID = 2L;
	
	public static final String TEST_USERNAME = "testuser";
	
	private static final String BEING_PREFIX = "src/test/resources/being-";
	private static final String BEING_SUFFIX = ".json";
	
	private static final String MUD_PLAYER_PREFIX = "src/test/resources/mudplayer-";
	private static final String MUD_PLAYER_SUFFIX = ".json";
	
	private static final String MUD_SESSION_PREFIX = "src/test/resources/mudsession-";
	private static final String MUD_SESSION_SUFFIX = ".json";

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
	
	public static MudSession loadMudSession(String username) throws IOException{
		
		return jsonMapper.readValue(new File(
				PlayerTestData.MUD_SESSION_PREFIX +
				username + 
				PlayerTestData.MUD_SESSION_SUFFIX
				), MudSession.class);
	}
}
