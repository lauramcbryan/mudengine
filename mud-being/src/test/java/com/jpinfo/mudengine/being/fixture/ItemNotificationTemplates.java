package com.jpinfo.mudengine.being.fixture;

import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumEntity;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.function.impl.RegexFunction;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class ItemNotificationTemplates implements TemplateLoader {

	private static final String BASIC = "BASIC";
	
	private static final String ITEM_EVENT = "ITEM_EVENT";
	private static final String ITEM_OWNED_EVENT = "ITEM_OWNED_EVENT";
	private static final String ITEM_UNOWNED_EVENT = "ITEM_UNOWNED_EVENT";
	
	public static final String ITEM_DROP = "ITEM_DROP";
	
	public static final String ITEM_TAKEN = "ITEM_TAKEN";
	
	public static final String ITEM_OWNED_QTTY_INCREASE = "ITEM_OWNED_QTTY_INCREASE";
	public static final String ITEM_UNOWNED_QTTY_INCREASE = "ITEM_UNOWNED_QTTY_INCREASE";
	
	public static final String ITEM_OWNED_QTTY_DECREASE = "ITEM_OWNED_QTTY_DECREASE";
	public static final String ITEM_UNOWNED_QTTY_DECREASE = "ITEM_UNOWNED_QTTY_DECREASE";
	
	public static final String ITEM_OWNED_ATTR_CHANGE = "ITEM_OWNED_ATTR_CHANGE";
	public static final String ITEM_UNOWNED_ATTR_CHANGE = "ITEM_UNOWNED_ATTR_CHANGE";
	
	public static final String ITEM_OWNED_DESTROY = "ITEM_OWNED_DESTROY";
	public static final String ITEM_UNOWNED_DESTROY = "ITEM_UNOWNED_DESTROY";
	
	public static final String ITEM_OWNED_CLASS_CHANGE = "ITEM_OWNED_CLASS_CHANGE";
	public static final String ITEM_UNOWNED_CLASS_CHANGE = "ITEM_UNOWNED_CLASS_CHANGE";
	
	
	
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
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_EVENT).inherits(BASIC, new Rule() {{
			add("entity", EnumEntity.ITEM);
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_OWNED_EVENT).inherits(ITEM_EVENT, new Rule() {{
			add("targetEntity", EnumEntity.BEING);
			add("targetEntityId", random(Long.class));
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_UNOWNED_EVENT).inherits(ITEM_EVENT, new Rule() {{
			add("targetEntity", EnumEntity.PLACE);
			add("targetEntityId", random(Long.class));
		}});
		
		
		
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_DROP).inherits(ITEM_OWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_DROP);
		}});

		Fixture.of(NotificationMessage.class).addTemplate(ITEM_TAKEN).inherits(ITEM_OWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_TAKEN);
		}});
		
		
		
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_OWNED_QTTY_INCREASE).inherits(ITEM_OWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_QTTY_INCREASE);
		}});
		
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_UNOWNED_QTTY_INCREASE).inherits(ITEM_UNOWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_QTTY_INCREASE);
		}});
		
		
		
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_OWNED_QTTY_DECREASE).inherits(ITEM_OWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_QTTY_DECREASE);
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_UNOWNED_QTTY_DECREASE).inherits(ITEM_UNOWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_QTTY_DECREASE);
		}});

		
		
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_OWNED_ATTR_CHANGE).inherits(ITEM_OWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_ATTR_CHANGE);
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_UNOWNED_ATTR_CHANGE).inherits(ITEM_UNOWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_ATTR_CHANGE);
		}});

		
		
		

		Fixture.of(NotificationMessage.class).addTemplate(ITEM_OWNED_DESTROY).inherits(ITEM_OWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_DESTROY);
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_UNOWNED_DESTROY).inherits(ITEM_UNOWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_DESTROY);
		}});

		
		
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_OWNED_CLASS_CHANGE).inherits(ITEM_OWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_CLASS_CHANGE);
		}});
		
		Fixture.of(NotificationMessage.class).addTemplate(ITEM_UNOWNED_CLASS_CHANGE).inherits(ITEM_UNOWNED_EVENT, new Rule() {{
			add("event", EnumNotificationEvent.ITEM_CLASS_CHANGE);
		}});

		
	}

}
