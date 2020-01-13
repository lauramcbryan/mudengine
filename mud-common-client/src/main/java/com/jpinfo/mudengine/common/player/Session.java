package com.jpinfo.mudengine.common.player;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Session implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long sessionId;
	
	private Long playerId;
	
	private LocalDateTime sessionStart;
	
	private LocalDateTime sessionEnd;

	private String ipAddress;
	
	private String clientType;
	
	private Long beingCode;
	
	private String curWorldName;	
}
