package com.jpinfo.mudengine.common.player;

import java.util.ArrayList;
import java.util.List;

/**
 * The persistent class for the mud_player database table.
 * 
 */
public class Player implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int STATUS_PENDING = 0;
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_INACTIVE = 2;
	public static final int STATUS_BLOCKED = 3;
	public static final int STATUS_BANNED = 4;

	private Long playerId;

	private String username;
	
	private String email;
	
	private String locale;
	
	private Integer status;
	
	private List<PlayerBeing> beingList;

	public Player() {
		this.beingList = new ArrayList<PlayerBeing>();
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public List<PlayerBeing> getBeingList() {
		return beingList;
	}

	public void setBeingList(List<PlayerBeing> beingList) {
		this.beingList = beingList;
	}
}