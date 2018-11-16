package com.jpinfo.mudengine.common.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;

import lombok.Data;

@Data
public class MessageRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long senderCode;
	
	private String senderName;
	
	private String messageKey;
	
	private String[] args;
	
	private List<MessageEntity> changedEntities;
	
	
	
	public void addChangedEntity(EnumEntityType entityType, Long entityId) {
		
		if (changedEntities==null) {
			changedEntities = new ArrayList<>();
		}
		
		changedEntities.add(new MessageEntity(entityType, entityId));
	}
}
