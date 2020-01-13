package com.jpinfo.mudengine.common.player;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PlayerBeing {
	
	private Long beingCode;
	
	private String beingName;
	
	private String beingClass;
	
	private LocalDateTime lastPlayed;
}
