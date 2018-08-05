package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeingSkill;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillPK;

public class MudBeingSkillConverter {
	
	private MudBeingSkillConverter() { }
	
	public static MudBeingSkill build(Long beingCode, String skillCode, Integer skillValue) {
		
		MudBeingSkill dbSkill = new MudBeingSkill();
		MudBeingSkillPK dbSkillPK = new MudBeingSkillPK();
		
		dbSkillPK.setSkillCode(skillCode);
		dbSkillPK.setBeingCode(beingCode);
		
		dbSkill.setId(dbSkillPK);
		dbSkill.setSkillValue(skillValue);
		
		return dbSkill;
	}

}
