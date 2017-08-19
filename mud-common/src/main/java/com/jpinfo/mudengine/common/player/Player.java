package com.jpinfo.mudengine.common.player;

/**
 * The persistent class for the mud_player database table.
 * 
 */
public class Player extends PlayerSimpleData  {
	
	private static final long serialVersionUID = 1L;
	
	public static final int STATUS_PENDING = 0;
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_INACTIVE = 2;
	public static final int STATUS_BLOCKED = 3;
	public static final int STATUS_BANNED = 4;

	private Long playerId;

	private String username;
	
	private Integer status;

	public Player() {
	}

	public Long getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	
	
}