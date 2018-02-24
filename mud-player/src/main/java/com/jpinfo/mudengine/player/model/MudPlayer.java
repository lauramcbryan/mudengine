package com.jpinfo.mudengine.player.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="MUD_PLAYER")
@SequenceGenerator(name="mud_player_seq", sequenceName="mud_player_seq", allocationSize=1)
public class MudPlayer implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="mud_player_seq", strategy=GenerationType.SEQUENCE)
	@Column(name="player_id")
	private Long playerId;
	
	private String username;
	
	private String password;
	
	private String email;
	
	private String locale;
	
	private Date createDate;
	
	private Integer status;
	
	@OneToMany(mappedBy="id.playerId", cascade=CascadeType.ALL)
	private List<MudPlayerBeing> beingList;
	
	public MudPlayer() {

	}

	public Long getPlayerId() {
		return playerId;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public List<MudPlayerBeing> getBeingList() {
		return beingList;
	}

	public void setBeingList(List<MudPlayerBeing> beingList) {
		this.beingList = beingList;
	}
		
}
