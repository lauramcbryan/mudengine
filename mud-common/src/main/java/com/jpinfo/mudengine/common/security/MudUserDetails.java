package com.jpinfo.mudengine.common.security;

import java.util.Optional;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MudUserDetails {

	private Optional<Session> sessionData;
	
	private Optional<Player> playerData;
}
