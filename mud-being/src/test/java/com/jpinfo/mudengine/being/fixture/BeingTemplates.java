package com.jpinfo.mudengine.being.fixture;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.being.model.MudBeingSkill;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.MudBeingSlot;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrPK;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class BeingTemplates implements TemplateLoader {
	
	public static final String VALID = "VALID";
	public static final String PLAYABLE = "PLAYABLE";

	@Override
	public void load() {
		
		Fixture.of(MudBeingAttrPK.class).addTemplate(VALID, new Rule() {{
			
		}});
		
		Fixture.of(MudBeingAttr.class).addTemplate(VALID, new Rule() {{
			add("code", random(Integer.class));
			add("value", random(Integer.class));
		}});
		
		Fixture.of(MudBeingClass.class).addTemplate(VALID, new Rule() {{
			add("code", random(Long.class));
			add("name", regex("name-(\\d{4})"));
			add("description", random(Long.class));
			add("size", random(Integer.class));
			add("attrs", has(3).of(MudBeingClassAttr.class, VALID));
			add("skills", has(3).of(MudBeingClassSkill.class, VALID));
			
		}});

		Fixture.of(MudBeing.class).addTemplate(VALID, new Rule() {{
			add("code", random(Long.class));
			add("name", regex("name-(\\d{4})"));
			add("curWorld", regex("world-(\\d{4})"));
			add("curPlaceCode", random(Integer.class));
			add("playerId", random(Long.class));
			add("type", random(Integer.class, range(0, 3)));
			add("beingClass", one(MudBeingClass.class, VALID));
			add("attrs", has(3).of(MudBeingAttr.class, VALID));
			add("skills", has(3).of(MudBeingSkill.class, VALID));
			add("attrModifiers", has(3).of(MudBeingAttrModifier.class, VALID));
			add("skillModifiers", has(3).of(MudBeingSkillModifier.class, VALID));
			add("slots", has(5).of(MudBeingSlot.class, VALID));
		}});

	}

}
