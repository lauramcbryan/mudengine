package com.jpinfo.mudengine.message.fixture;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.message.MessageEntity;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.message.model.MudMessage;
import com.jpinfo.mudengine.common.message.MessageRequest;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.function.impl.RegexFunction;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class MessageTemplates implements TemplateLoader {

	public static final String VALID = "VALID";
	public static final String SENDER = "SENDER";
	public static final String ENTITIES = "ENTITIES";
	
	@Override
	public void load() {
		
		Fixture.of(Being.class).addTemplate(VALID, new Rule() {{
			add("code", random(Long.class));
			add("playerId", random(Long.class));
		}});
		
		Fixture.of(MessageEntity.class).addTemplate(VALID, new Rule() {{
			add("entityType", random(EnumEntityType.ITEM, EnumEntityType.BEING, EnumEntityType.PLACE));
			add("entityId", random(Long.class));
		}});
		
		Fixture.of(MessageRequest.class).addTemplate(VALID, new Rule() {{
			add("messageKey", regex("mkey-(\\d{4})"));
			add("args", new String[] {
					new RegexFunction("args-\\d{4}").generateValue(),
					new RegexFunction("args-\\d{4}").generateValue(),
					new RegexFunction("args-\\d{4}").generateValue()
			});
		}});

		Fixture.of(MessageRequest.class).addTemplate(SENDER).inherits(VALID, new Rule() {{
			add("senderCode", random(Long.class));
			add("senderName", regex("sender-(\\d{4})"));
		}});
		
		Fixture.of(MessageRequest.class).addTemplate(ENTITIES).inherits(VALID, new Rule() {{
			add("changedEntities", has(2).of(MessageEntity.class, VALID));
		}});
		
		Fixture.of(MudMessage.class).addTemplate(VALID, new Rule() {{
			
			add("messageId", random(Long.class));
			add("beingCode", random(Long.class));
			add("senderCode", random(Long.class));
			add("senderName", regex("Sender-(\\d{4})"));
			add("messageKey", regex("mkey-(\\d{4})"));
			add("readFlag", random(Boolean.class));
			add("insertDate", new java.sql.Timestamp(System.currentTimeMillis()));
		}});
		
	}

}
