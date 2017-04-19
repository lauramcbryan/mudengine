package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the mud_player database table.
 * 
 */
@Entity
@Table(name="MUD_PLAYER")
public class MudPlayer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="player_id")
	private Long playerId;

	private String login;

	private String name;

	private String password;

	public MudPlayer() {
	}

	public Long getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}