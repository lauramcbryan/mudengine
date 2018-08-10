package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;

public class MudBeingSkillModifierConverter {

	private MudBeingSkillModifierConverter() { }
	
	public static MudBeingSkillModifier convert(Long beingCode, BeingSkillModifier curSkillModifier) {
		
		MudBeingSkillModifier newDbSkillModifier = new MudBeingSkillModifier();
		MudBeingSkillModifierPK newDbSkillModifierPK = new MudBeingSkillModifierPK();
		
		newDbSkillModifierPK.setBeingCode(beingCode);
		newDbSkillModifierPK.setCode(curSkillModifier.getCode());
		newDbSkillModifierPK.setOriginCode(curSkillModifier.getOriginCode());
		newDbSkillModifierPK.setOriginType(curSkillModifier.getOriginType());
		
		newDbSkillModifier.setId(newDbSkillModifierPK);
		newDbSkillModifier.setOffset(curSkillModifier.getOffset());
		newDbSkillModifier.setEndTurn(curSkillModifier.getEndTurn());
		
		return newDbSkillModifier;
	}
	
	public static MudBeing sync(MudBeing dbBeing, Being requestBeing) {
		
		if (requestBeing.getSkillModifiers()!=null) {

			// Looking for skillModifiers to delete
			dbBeing.getSkillModifiers().removeIf(d -> 
				
				requestBeing.getSkillModifiers().stream()
					.noneMatch(e -> d.getId().getCode().equals(e.getCode()))
			);
			
			// Looking for skillModifiers to add/update
			requestBeing.getSkillModifiers().stream()
				.forEach(requestSkill -> {
					
					// Search for the skillModifier in database record
					MudBeingSkillModifier skillMod = 
					dbBeing.getSkillModifiers().stream()
						.filter(e -> e.getId().getCode().equals(requestSkill.getCode()))
						.findFirst()
						.orElse(MudBeingSkillModifierConverter.convert(dbBeing.getCode(), requestSkill));
					
					skillMod.setOffset(requestSkill.getOffset());
				
					dbBeing.getSkillModifiers().add(skillMod);
			});
		}
		
		return dbBeing;
	}
	
	public static MudBeing sync(MudBeing dbBeing, MudBeingClass previousClass, MudBeingClass nextClass) {
		
		if (previousClass!=null) {
			
			// Looking for attrModifiers to delete
			dbBeing.getSkillModifiers().removeIf(d -> {
				
				boolean existsInOldClass = 
						previousClass.getSkills().stream()
						.anyMatch(e -> d.getId().getCode().equals(e.getId().getCode()));
				
				boolean existsInNewClass =
						nextClass.getSkills().stream()
						.anyMatch(e -> d.getId().getCode().equals(e.getId().getCode()));
				
				return existsInOldClass && !existsInNewClass;
			});
		}
		
		return dbBeing;
		
	}

}
