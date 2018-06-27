package com.jpinfo.mudengine.player.model;

import java.util.Date;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name="MUD_PLAYER_SESSION")
@SequenceGenerator(name="mud_session_seq", sequenceName="mud_session_seq", allocationSize=1)
@Data
public class MudSession {

	@Id
	@GeneratedValue(generator="mud_session_seq", strategy=GenerationType.SEQUENCE)
	private Long sessionId;
	
	@Column(name="player_id", insertable=false, updatable=false)
	private Long playerId;
	
	@ManyToOne
	@JoinColumn(name="player_id", referencedColumnName="player_id")
	private MudPlayer player;
	
	@Column(name="session_start")
	private Date sessionStart;
	
	@Column(name="session_end")
	private Date sessionEnd;
	
	private String clientType;
	
	private String ipAddress;

	@Column(nullable=true)
	private Long beingCode;	
}
