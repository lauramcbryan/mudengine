package com.jpinfo.mudengine.being.fixture;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.being.model.MudBeingClassSlot;
import com.jpinfo.mudengine.being.model.MudBeingSkill;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.MudBeingSlot;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingClassAttrPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingClassSkillPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingClassSlotPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSlotPK;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.being.Being;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class BeingTemplates implements TemplateLoader {
	
	public static final String VALID = "VALID";
	public static final String SIMPLE = "SIMPLE";
	public static final String PLAYABLE = "PLAYABLE";
	public static final String PLAYABLE_WITH_MODIFIERS = "PLAYABLE_WITH_MODIFIERS";

	@Override
	public void load() {

		Fixture.of(MudBeingClassSlotPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("slotCode-(\\d{4})"));
		}});
		
		Fixture.of(MudBeingClassSlot.class).addTemplate(VALID, new Rule() {{
			add("id", one(MudBeingClassSlotPK.class, VALID));
		}});

		Fixture.of(MudBeingClassSkillPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("attrCode-(\\d{4})"));
		}});
		
		
		Fixture.of(MudBeingClassSkill.class).addTemplate(VALID, new Rule() {{
			add("id", one(MudBeingClassSkillPK.class, VALID));
			add("value", random(Integer.class, range(1,100)));
		}});

		Fixture.of(MudBeingClassAttrPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("attrCode-(\\d{4})"));
		}});
		
		Fixture.of(MudBeingClassAttr.class).addTemplate(VALID, new Rule() {{
			add("id", one(MudBeingClassAttrPK.class, VALID));
			add("value", random(Integer.class, range(1,100)));
		}});
		
		Fixture.of(MudBeingClass.class).addTemplate(VALID, new Rule() {{
			add("code", regex("code-(\\d{4})"));
			add("name", regex("name-(\\d{4})"));
			add("description", regex("desc-(\\d{4})"));
			add("size", random(Integer.class));
			add("attrs", has(3).of(MudBeingClassAttr.class, VALID));
			add("skills", has(3).of(MudBeingClassSkill.class, VALID));
			add("slots", has(3).of(MudBeingClassSlot.class, VALID));
			
		}});
		
		Fixture.of(MudBeingSlotPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("slotCode-(\\d{4})"));
		}});
		
		Fixture.of(MudBeingSlot.class).addTemplate(VALID, new Rule() {{
			add("id", one(MudBeingSlotPK.class, VALID));
			add("itemCode", random(Long.class));
			
		}});

		Fixture.of(MudBeingSkillPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("skillCode-(\\d{4})"));
		}});
		
		Fixture.of(MudBeingSkill.class).addTemplate(VALID, new Rule() {{
			add("id", one(MudBeingSkillPK.class, VALID));
			add("value", random(Integer.class, range(1,100)));
		}});

		Fixture.of(MudBeingAttrPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("attrCode-(\\d{4})"));
		}});
		
		Fixture.of(MudBeingAttr.class).addTemplate(VALID, new Rule() {{
			add("id", one(MudBeingAttrPK.class, VALID));
			add("value", random(Integer.class, range(1,100)));
		}});

		Fixture.of(MudBeingSkillModifierPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("skillModCode-(\\d{4})"));
			add("originCode", regex("originCode-(\\d{4})"));
			add("originType", regex("originType-(\\d{4})"));
		}});
		
		Fixture.of(MudBeingSkillModifier.class).addTemplate(VALID, new Rule() {{
			add("id", one(MudBeingSkillModifierPK.class, VALID));
			add("offset", random(Float.class));
			add("endTurn", random(Integer.class));
		}});		
		
		
		Fixture.of(MudBeingAttrModifierPK.class).addTemplate(VALID, new Rule() {{
			add("code", regex("attrModCode-(\\d{4})"));
			add("originCode", regex("originCode-(\\d{4})"));
			add("originType", regex("originType-(\\d{4})"));
		}});
		
		Fixture.of(MudBeingAttrModifier.class).addTemplate(VALID, new Rule() {{
			add("id", one(MudBeingAttrModifierPK.class, VALID));
			add("offset", random(Float.class));
			add("endTurn", random(Integer.class));
		}});
		
		Fixture.of(MudBeing.class).addTemplate(SIMPLE, new Rule() {{
			add("code", random(Long.class));
			add("name", regex("name-(\\d{4})"));
			add("curWorld", regex("world-(\\d{4})"));
			add("type", Being.enumBeingType.REGULAR_NON_SENTIENT.ordinal());
			add("curPlaceCode", random(Integer.class));
			add("type", random(Integer.class, range(0, 3)));
			add("beingClass", one(MudBeingClass.class, VALID));
			add("quantity", BeingHelper.CREATE_DEFAULT_QUANTITY);
			//add("attrs", has(3).of(MudBeingAttr.class, VALID));
			//add("skills", has(3).of(MudBeingSkill.class, VALID));
			//add("attrModifiers", has(3).of(MudBeingAttrModifier.class, VALID));
			//add("skillModifiers", has(3).of(MudBeingSkillModifier.class, VALID));
			//add("slots", has(5).of(MudBeingSlot.class, VALID));
		}});
		
		Fixture.of(MudBeing.class).addTemplate(PLAYABLE).inherits(SIMPLE, new Rule() {{
			add("playerId", random(Long.class));
			add("type", Being.enumBeingType.PLAYABLE.ordinal());
		}});
		
		Fixture.of(MudBeing.class).addTemplate(PLAYABLE_WITH_MODIFIERS).inherits(PLAYABLE, new Rule() {{
			add("attrModifiers", has(3).of(MudBeingAttrModifier.class, VALID));
			add("skillModifiers", has(3).of(MudBeingSkillModifier.class, VALID));
		}});
	}

}
