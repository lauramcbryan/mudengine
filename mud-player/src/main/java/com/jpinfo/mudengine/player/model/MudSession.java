package com.jpinfo.mudengine.player.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="MUD_PLAYER_SESSION")
@SequenceGenerator(name="mud_session_seq", sequenceName="mud_session_seq")
public class MudSession {

	@Id
	@GeneratedValue(generator="mud_session_seq", strategy=GenerationType.SEQUENCE)
	private Long sessionId;
	
	@ManyToOne
	@JoinColumn(name="player_id", referencedColumnName="player_id")
	private MudPlayer player;
	
	@Column(name="session_start")
	private Date sessionStart;
	
	@Column(name="session_end")
	private Date sessionEnd;
	
	@Column(name="notification_able")
	private Boolean notificationAble;
	
	public MudSession() {
		
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
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

	public Boolean getNotificationAble() {
		return notificationAble;
	}

	public void setNotificationAble(Boolean notificationAble) {
		this.notificationAble = notificationAble;
	}

	public MudPlayer getPlayer() {
		return player;
	}

	public void setPlayer(MudPlayer player) {
		this.player = player;
	}
	
	
	
}
