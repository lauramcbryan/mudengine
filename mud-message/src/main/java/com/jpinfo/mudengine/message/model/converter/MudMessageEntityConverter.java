package com.jpinfo.mudengine.message.model.converter;

import com.jpinfo.mudengine.common.message.MessageEntity;
import com.jpinfo.mudengine.message.model.MudMessageEntity;
import com.jpinfo.mudengine.message.model.pk.MudMessageEntityPK;

public class MudMessageEntityConverter {

	private MudMessageEntityConverter() { }
	
	public static MudMessageEntity build(final Long messageId, final MessageEntity requestEntity) {
		
		MudMessageEntity result = new MudMessageEntity();
		MudMessageEntityPK pk = new MudMessageEntityPK();

		pk.setMessageId(messageId);
		pk.setEntityId(requestEntity.getEntityId());
		pk.setEntityType(requestEntity.getEntityType().toString());
		
		result.setId(pk);
		return result;
	}
}
