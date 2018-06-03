package com.jpinfo.mudengine.client.api;

import com.jpinfo.mudengine.common.player.Player;

public class ApiResult {

	private String changedAuthToken;
	
	private Player updatedPlayerData;
	
	public ApiResult() {
		
	}
	
	public ApiResult(String authToken, Player playerData) {
		
		this.changedAuthToken = authToken;
		this.updatedPlayerData = playerData;
	}

	public String getChangedAuthToken() {
		return changedAuthToken;
	}

	public void setChangedAuthToken(String changedAuthToken) {
		this.changedAuthToken = changedAuthToken;
	}

	public Player getUpdatedPlayerData() {
		return updatedPlayerData;
	}

	public void setUpdatedPlayerData(Player updatedPlayerData) {
		this.updatedPlayerData = updatedPlayerData;
	}
}
