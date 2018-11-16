package com.jpinfo.mudengine.common.message;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	

	public enum EnumEntityType {ITEM, BEING, PLACE}
	
	private EnumEntityType entityType;
	
	private Long entityId;
}
