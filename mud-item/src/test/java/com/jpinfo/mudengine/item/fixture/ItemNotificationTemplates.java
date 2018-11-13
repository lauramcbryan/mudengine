package com.jpinfo.mudengine.item.fixture;

import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumEntity;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.function.impl.RegexFunction;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class ItemNotificationTemplates implements TemplateLoader {

	private static final String BASIC = "BASIC";
	public static final String PLACE_DESTROY_EVENT = "PLACE_DESTROY_EVENT";
	public static final String BEING_DESTROY_EVENT = "BEING_DESTROY_EVENT";
	
	@Override
	public void load() {
		
		Fixture.of(NotificationMessage.class).addTemplate(BASIC, new Rule() {{
			add("entityId", random(Long.class));
			add("messageKey", regex("mkey-\\d{4}"));
			add("args", new String[] {
					new RegexFunction("args-\\d{4}").generateValue(),
					new RegexFunction("args-\\d{4}").generateValue(),
					new RegexFunction("args-\\d{4}").generateValue()
			});
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(PLACE_DESTROY_EVENT).inherits(BASIC, new Rule() {{
			add("entity", EnumEntity.PLACE);
			add("event", EnumNotificationEvent.PLACE_DESTROY);
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(BEING_DESTROY_EVENT).inherits(BASIC, new Rule() {{
			add("entity", EnumEntity.BEING);
			add("event", EnumNotificationEvent.BEING_DESTROY);
			add("targetEntity", EnumEntity.BEING);
			add("targetEntityId", random(Long.class));
		}});
		

	}

}
