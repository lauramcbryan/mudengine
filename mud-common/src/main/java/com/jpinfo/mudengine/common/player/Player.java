package com.jpinfo.mudengine.common.player;

import java.io.Serializable;

/**
 * The persistent class for the mud_player database table.
 * 
 */
public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int STATUS_PENDING = 0;
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_INACTIVE = 2;
	public static final int STATUS_BLOCKED = 3;
	public static final int STATUS_BANNED = 4;

	private Long playerId;

	private String username;

	private String name;
	
	private String email;
	
	private String language;
	
	private String country;
	
	private Integer status;

	public Player() {
	}

	public Long getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	
	
}