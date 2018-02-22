package com.jpinfo.mudengine.common.player;

import java.util.Date;

public class PlayerBeing {
	
	private Long beingCode;
	
	private String beingName;
	
	private String beingClass;
	
	private Date lastPlayed;

	public Long getBeingCode() {
		return beingCode;
	}

	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}

	public String getBeingName() {
		return beingName;
	}

	public void setBeingName(String beingName) {
		this.beingName = beingName;
	}

	public String getBeingClass() {
		return beingClass;
	}

	public void setBeingClass(String beingClass) {
		this.beingClass = beingClass;
	}

	public Date getLastPlayed() {
		return lastPlayed;
	}

	public void setLastPlayed(Date lastPlayed) {
		this.lastPlayed = lastPlayed;
	}
}
