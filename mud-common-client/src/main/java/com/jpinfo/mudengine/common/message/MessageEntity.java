package com.jpinfo.mudengine.common.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class MessageEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	

	public enum EnumEntityType {ITEM, BEING, PLACE}
	
	private EnumEntityType entityType;
	
	private Long entityId;
}
