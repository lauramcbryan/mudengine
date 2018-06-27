package com.jpinfo.mudengine.common.player;

import java.util.Date;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Session implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long sessionId;
	
	private Long playerId;
	
	private Date sessionStart;
	
	private Date sessionEnd;

	private String ipAddress;
	
	private String clientType;
	
	private Long beingCode;
	
	public Session(Map<String, Object> map) {
		
		this.sessionId = new Long(String.valueOf(map.get("sessionId")));
		this.playerId = new Long(String.valueOf(map.get("playerId")));
		
		if (map.get("sessionStart")!=null)
			this.sessionStart = new Date((Long)map.get("sessionStart"));
		
		if (map.get("sessionEnd")!=null)
			this.sessionEnd = new Date((Long)map.get("sessionEnd"));
		
		this.ipAddress = (String)map.get("ipAddress");
		this.clientType = (String)map.get("clientType");
		
		if (map.get("beingCode")!=null)
			this.beingCode = new Long(String.valueOf(map.get("beingCode")));
	}
}
