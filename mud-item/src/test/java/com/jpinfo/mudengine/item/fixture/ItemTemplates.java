package com.jpinfo.mudengine.item.fixture;

import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemAttr;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.model.MudItemClassAttr;
import com.jpinfo.mudengine.item.model.pk.MudItemAttrPK;
import com.jpinfo.mudengine.item.model.pk.MudItemClassAttrPK;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class ItemTemplates implements TemplateLoader {
	
	public static final String REQUEST = "REQUEST";
	public static final String REQUEST_WITH_PLACE = "REQUEST_WITH_PLACE";
	public static final String REQUEST_WITH_OWNER = "REQUEST_WITH_OWNER";
	public static final String REQUEST_WITH_BOTH = "REQUEST_WITH_BOTH";

	public static final String RESPONSE = "RESPONSE";
	public static final String RESPONSE_FULL = "RESPONSE_FULL";
	public static final String RESPONSE_WITH_PLACE = "RESPONSE_WITH_PLACE";
	public static final String RESPONSE_WITH_OWNER = "RESPONSE_WITH_OWNER";

	public static final String DURATION = "DURATION";
	public static final String VALID = "VALID";
	
	@Override
	public void load() {
		
		Fixture.of(MudItemClassAttrPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("attr-(\\d{4})"));
		}});
		
		Fixture.of(MudItemClassAttr.class).addTemplate(ItemTemplates.VALID, new Rule () {{
			add("id", one(MudItemClassAttrPK.class, VALID));
			add("value", random(Integer.class));
		}});
		
		Fixture.of(MudItemClass.class).addTemplate(VALID, new Rule() {{
			add("code", regex("class(\\d{4})"));
			add("name", regex("className(\\d{4})"));
			add("size", random(Float.class));
			add("weight", random(Float.class));
			add("description", regex("description-(\\d{4})"));
			add("attrs", has(2).of(MudItemClassAttr.class, VALID));
			/**
			 * As demised item class needs to be an specific code, based in an existing item class
			 * we'll not set it here.
			 */
		}});

		Fixture.of(MudItemAttrPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("attr-(\\d{4})"));
		}});
	
		Fixture.of(MudItemAttr.class).addTemplate(ItemTemplates.VALID, new Rule () {{
			add("id", one(MudItemAttrPK.class, VALID));
			add("value", random(Integer.class));
		}});
				
		Fixture.of(MudItem.class).addTemplate(ItemTemplates.REQUEST, new Rule() {{
			add("quantity", 1);
			add("itemClass", one(MudItemClass.class, VALID));
		}});
		
		Fixture.of(MudItem.class).addTemplate(ItemTemplates.REQUEST_WITH_OWNER).inherits(REQUEST, new Rule() {{
			add("curOwner", random(Long.class));
		}});
		
		Fixture.of(MudItem.class).addTemplate(ItemTemplates.REQUEST_WITH_PLACE).inherits(REQUEST, new Rule() {{
			add("curPlaceCode", random(Integer.class));
			add("curWorld", regex("world-(\\d{4})"));
		}});
		
		Fixture.of(MudItem.class).addTemplate(ItemTemplates.REQUEST_WITH_BOTH).inherits(REQUEST, new Rule() {{
			add("curOwner", random(Long.class));
			add("curPlaceCode", random(Integer.class));
			add("curWorld", regex("world-(\\d{4})"));
		}});
		
		
		Fixture.of(MudItem.class).addTemplate(ItemTemplates.RESPONSE).inherits(REQUEST, new Rule() {{
			add("code", random(Long.class));
		}});

		Fixture.of(MudItem.class).addTemplate(ItemTemplates.RESPONSE_WITH_OWNER).inherits(RESPONSE, new Rule() {{
			add("curOwner", random(Long.class));
		}});
		
		Fixture.of(MudItem.class).addTemplate(ItemTemplates.RESPONSE_WITH_PLACE).inherits(RESPONSE, new Rule() {{
			add("curPlaceCode", random(Integer.class));
			add("curWorld", regex("world-(\\d{4})"));
		}});

		Fixture.of(MudItem.class).addTemplate(ItemTemplates.RESPONSE_FULL).inherits(RESPONSE, new Rule() {{
			add("curPlaceCode", random(Integer.class));
			add("curWorld", regex("world-(\\d{4})"));
			add("attrs", has(2).of(MudItemAttr.class, VALID));
		}});
		
	}

}
