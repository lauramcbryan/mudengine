package com.jpinfo.mudengine.common.player;

import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
public class Session implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long sessionId;
	
	private Long playerId;
	
	private Date sessionStart;
	
	private Date sessionEnd;

	private String ipAddress;
	
	private String clientType;
	
	private Long beingCode;
	
	private String curWorldName;
	
	public Session() { }
	
	public Session(Map<String, Object> map) {
		
		this.sessionId = Long.valueOf(String.valueOf(map.get("sessionId")));
		this.playerId = Long.valueOf(String.valueOf(map.get("playerId")));
		
		if (map.get("sessionStart")!=null)
			this.sessionStart = new Date((Long)map.get("sessionStart"));
		
		if (map.get("sessionEnd")!=null)
			this.sessionEnd = new Date((Long)map.get("sessionEnd"));
		
		this.ipAddress = (String)map.get("ipAddress");
		this.clientType = (String)map.get("clientType");
		
		if (map.get("beingCode")!=null)
			this.beingCode = Long.valueOf(String.valueOf(map.get("beingCode")));
		
		this.curWorldName = (String)map.get("curWorldName");
	}
}
