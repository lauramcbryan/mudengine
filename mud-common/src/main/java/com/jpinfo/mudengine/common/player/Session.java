package com.jpinfo.mudengine.common.player;

import java.util.Date;
import java.util.Map;

public class Session implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long sessionId;
	
	private Long playerId;
	
	private Date sessionStart;
	
	private Date sessionEnd;

	private String locale;
	
	private String ipAddress;
	
	private String clientType;
	
	private Long beingCode;
	
	public Session() {
		
	}
	
	public Session(Map<String, Object> map) {
		
		this.sessionId = new Long(String.valueOf(map.get("sessionId")));
		this.playerId = new Long(String.valueOf(map.get("playerId")));
		this.sessionStart = (Date)map.get("sessionStart");
		this.sessionEnd = (Date)map.get("sessionEnd");;
		this.locale = (String)map.get("locale");
		this.ipAddress = (String)map.get("ipAddress");
		this.clientType = (String)map.get("clientType");
		
		if (map.get("beingCode")!=null)
			this.beingCode = new Long(String.valueOf(map.get("beingCode")));
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

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public Long getBeingCode() {
		return beingCode;
	}

	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}
}
