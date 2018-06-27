package com.jpinfo.mudengine.common.player;

import java.util.Date;

import lombok.Data;

@Data
public class PlayerBeing {
	
	private Long beingCode;
	
	private String beingName;
	
	private String beingClass;
	
	private Date lastPlayed;
}
