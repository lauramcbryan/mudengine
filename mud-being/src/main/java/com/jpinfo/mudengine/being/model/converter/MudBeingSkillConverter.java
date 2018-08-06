package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.being.model.MudBeingSkill;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillPK;
import com.jpinfo.mudengine.common.being.Being;

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
	
	public static MudBeingSkill convert(Long beingCode, MudBeingClassSkill classSkill) {
		
		MudBeingSkill dbSkill = new MudBeingSkill();
		MudBeingSkillPK dbSkillPK = new MudBeingSkillPK();
		
		dbSkillPK.setSkillCode(classSkill.getId().getSkillCode());
		dbSkillPK.setBeingCode(beingCode);
		
		dbSkill.setId(dbSkillPK);
		dbSkill.setSkillValue(classSkill.getSkillValue());
		
		return dbSkill;
	}
	
	
	

	public static MudBeing sync(MudBeing dbBeing, MudBeingClass previousClass, MudBeingClass nextClass) {

		// Looking for skills to remove
		if (previousClass!=null) {
			
			// Looking for attributes to remove
			dbBeing.getSkills().removeIf(d -> {
				
				boolean existsInOldClass = previousClass.getSkills().stream()
						.anyMatch(e -> d.getId().getSkillCode().equals(e.getId().getSkillCode()));
				
				boolean existsInNewClass = nextClass.getSkills().stream()
						.anyMatch(e -> d.getId().getSkillCode().equals(e.getId().getSkillCode()));
				
				return existsInOldClass && ! existsInNewClass;
			});
		}
		
		// Looking for skills to add/update
		nextClass.getSkills().stream()
			.forEach(d -> {

				MudBeingSkill skill = 
					dbBeing.getSkills().stream()
						.filter(e -> e.getId().getSkillCode().equals(d.getId().getSkillCode()))
						.findFirst()
						.orElse(MudBeingSkillConverter.convert(dbBeing.getBeingCode(), d));

				// Update the attribute value
				skill.setSkillValue(d.getSkillValue());
				
				dbBeing.getSkills().add(skill);				
			});
		
		return dbBeing;
	}
	
	public static MudBeing sync(MudBeing dbBeing, Being requestBeing) {

		// Looking for skills to remove
		dbBeing.getSkills().removeIf(d -> 
			!requestBeing.getSkills().containsKey(d.getId().getSkillCode())
		);
		
		// Looking for skills to add/update
		requestBeing.getSkills().keySet().stream()
			.forEach(requestSkillCode -> {

				Integer requestSkillValue = requestBeing.getSkills().get(requestSkillCode);
				
				MudBeingSkill skill = 
					dbBeing.getSkills().stream()
						.filter(d -> d.getId().getSkillCode().equals(requestSkillCode))
						.findFirst()
						.orElse(MudBeingSkillConverter.build(
								dbBeing.getBeingCode(), requestSkillCode, requestSkillValue));
				
				
				// Update the skill value
				skill.setSkillValue(requestSkillValue);
				
				dbBeing.getSkills().add(skill);
				
			});
		
		return dbBeing;
	}

}
