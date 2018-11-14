package com.jpinfo.mudengine.common.message;

import lombok.Data;

@Data
public class MessageEntity {
	
	public enum EnumEntityType {ITEM, BEING, PLACE}
	
	private EnumEntityType entityType;
	
	private Long entityId;
}
