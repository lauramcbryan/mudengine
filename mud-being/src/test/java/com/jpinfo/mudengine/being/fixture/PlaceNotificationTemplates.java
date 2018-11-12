package com.jpinfo.mudengine.being.fixture;

import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumEntity;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.function.impl.RegexFunction;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class PlaceNotificationTemplates implements TemplateLoader {
	
	private static final String BASIC = "BASIC";
	private static final String PLACE_EVENT = "PLACE_EVENT";
	
	public static final String PLACE_EXIT_CREATE = "PLACE_EXIT_CREATE";
	public static final String PLACE_EXIT_DESTROY = "PLACE_EXIT_DESTROY";
	public static final String PLACE_EXIT_OPEN = "PLACE_EXIT_OPEN";
	public static final String PLACE_EXIT_CLOSE = "PLACE_EXIT_CLOSE";
	public static final String PLACE_EXIT_UNLOCK = "PLACE_EXIT_UNLOCK";
	public static final String PLACE_EXIT_LOCK = "PLACE_EXIT_LOCK";
	public static final String PLACE_CLASS_CHANGE = "PLACE_CLASS_CHANGE";
	public static final String PLACE_DESTROY = "PLACE_DESTROY";


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
		
		Fixture.of(NotificationMessage.class).addTemplate(PLACE_EVENT).inherits(BASIC, new Rule() {{
			add("entity", EnumEntity.PLACE);
			add("targetEntity", EnumEntity.PLACE);
			add("targetEntityId", random(Long.class));
		}});
		
		
		Fixture.of(NotificationMessage.class).addTemplate(PLACE_DESTROY).inherits(PLACE_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.PLACE_DESTROY);
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(PLACE_EXIT_CREATE).inherits(PLACE_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.PLACE_EXIT_CREATE);
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(PLACE_EXIT_DESTROY).inherits(PLACE_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.PLACE_EXIT_DESTROY);
		}});

		Fixture.of(NotificationMessage.class).addTemplate(PLACE_EXIT_OPEN).inherits(PLACE_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.PLACE_EXIT_OPEN);
		}});

		Fixture.of(NotificationMessage.class).addTemplate(PLACE_EXIT_CLOSE).inherits(PLACE_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.PLACE_EXIT_CLOSE);
		}});

		Fixture.of(NotificationMessage.class).addTemplate(PLACE_EXIT_UNLOCK).inherits(PLACE_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.PLACE_EXIT_UNLOCK);
		}});

		Fixture.of(NotificationMessage.class).addTemplate(PLACE_EXIT_LOCK).inherits(PLACE_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.PLACE_EXIT_LOCK);
		}});

		Fixture.of(NotificationMessage.class).addTemplate(PLACE_CLASS_CHANGE).inherits(PLACE_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.PLACE_CLASS_CHANGE);
		}});

	}
}
