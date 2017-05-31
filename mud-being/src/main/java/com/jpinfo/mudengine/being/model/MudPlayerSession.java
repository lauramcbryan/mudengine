package com.jpinfo.mudengine.being.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="MUD_PLAYER_SESSION")
@SequenceGenerator(name="MUD_SESSION_SEQ", sequenceName="MUD_SESSION_SEQ")
public class MudPlayerSession {
	
	@Id
	@Column
	@GeneratedValue(generator="MUD_SESSION_SEQ", strategy=GenerationType.SEQUENCE)
	private Long sessionId;
	
	@Column(name="PLAYER_ID")
	private Long playerId;
	
	@Column
	private Date sessionStart;
	
	@Column
	private Date sessionEnd;
	
	@Column
	private String country;
	
	@Column
	private boolean notificationAble;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PLAYER_ID", referencedColumnName="PLAYER_ID", insertable=false, updatable=false)
	private MudPlayer player;
	
	public MudPlayerSession() {
		
	}


	public Long getSessionId() {
		return sessionId;
	}


	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}


	public Long getPlayerId() {
		return playerId;
	}


	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}


	public Date getSessionStart() {
		return sessionStart;
	}


	public void setSessionStart(Date sessionStart) {
		this.sessionStart = sessionStart;
	}


	public Date getSessionEnd() {
		return sessionEnd;
	}


	public void setSessionEnd(Date sessionEnd) {
		this.sessionEnd = sessionEnd;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public boolean isNotificationAble() {
		return notificationAble;
	}


	public void setNotificationAble(boolean notificationAble) {
		this.notificationAble = notificationAble;
	}


	public MudPlayer getPlayer() {
		return player;
	}
	
}
