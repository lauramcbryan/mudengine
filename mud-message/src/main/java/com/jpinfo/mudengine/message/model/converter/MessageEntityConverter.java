package com.jpinfo.mudengine.message.model.converter;

import com.jpinfo.mudengine.common.message.MessageEntity;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.message.model.MudMessageEntity;

public class MessageEntityConverter {

	private MessageEntityConverter() { }
	
	public static MessageEntity build(MudMessageEntity dbEntity) {
		
		MessageEntity result = new MessageEntity();
		
		result.setEntityId(dbEntity.getId().getEntityId());
		result.setEntityType(EnumEntityType.valueOf(dbEntity.getId().getEntityType()));
		
		return result;
	}
}
