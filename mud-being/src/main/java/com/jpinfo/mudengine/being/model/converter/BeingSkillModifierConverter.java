package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;

public class BeingSkillModifierConverter {

	// Some empty private No-Arg Constructor just to 
	// avoid SonarLint to point fingers at me
	private BeingSkillModifierConverter() { }
	
	public static BeingSkillModifier convert(MudBeingSkillModifier dbModifier) {
		
		BeingSkillModifier result = new BeingSkillModifier();
		
		result.setCode(dbModifier.getId().getCode());
		result.setOriginCode(dbModifier.getId().getOriginCode());
		result.setOriginType(dbModifier.getId().getOriginType());
		result.setOffset(dbModifier.getOffset());
		result.setEndTurn(dbModifier.getEndTurn());
		
		return result;
	}
}
