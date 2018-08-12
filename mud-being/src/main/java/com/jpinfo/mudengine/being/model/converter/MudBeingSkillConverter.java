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
		
		dbSkillPK.setCode(skillCode);
		dbSkillPK.setBeingCode(beingCode);
		
		dbSkill.setId(dbSkillPK);
		dbSkill.setValue(skillValue);
		
		return dbSkill;
	}
	
	public static MudBeingSkill convert(Long beingCode, MudBeingClassSkill classSkill) {
		
		MudBeingSkill dbSkill = new MudBeingSkill();
		MudBeingSkillPK dbSkillPK = new MudBeingSkillPK();
		
		dbSkillPK.setCode(classSkill.getId().getCode());
		dbSkillPK.setBeingCode(beingCode);
		
		dbSkill.setId(dbSkillPK);
		dbSkill.setValue(classSkill.getValue());
		
		return dbSkill;
	}
	
	
	

	public static MudBeing sync(MudBeing dbBeing, MudBeingClass previousClass, MudBeingClass nextClass) {

		// Looking for skills to remove
		if (previousClass!=null) {
			
			// Looking for attributes to remove
			dbBeing.getSkills().removeIf(d -> {
				
				boolean existsInOldClass = previousClass.getSkills().stream()
						.anyMatch(e -> d.getId().getCode().equals(e.getId().getCode()));
				
				boolean existsInNewClass = nextClass.getSkills().stream()
						.anyMatch(e -> d.getId().getCode().equals(e.getId().getCode()));
				
				return existsInOldClass && ! existsInNewClass;
			});
		}
		
		// Looking for skills to add/update
		nextClass.getSkills().stream()
			.forEach(d -> {

				MudBeingSkill skill = 
					dbBeing.getSkills().stream()
						.filter(e -> e.getId().getCode().equals(d.getId().getCode()))
						.findFirst()
						.orElse(MudBeingSkillConverter.convert(dbBeing.getCode(), d));

				// Update the attribute value
				skill.setValue(d.getValue());
				
				dbBeing.getSkills().add(skill);				
			});
		
		return dbBeing;
	}
	
	public static MudBeing sync(MudBeing dbBeing, Being requestBeing) {

		// Looking for skills to remove
		dbBeing.getSkills().removeIf(d -> 
			!requestBeing.getSkills().containsKey(d.getId().getCode())
		);
		
		// Looking for skills to add/update
		requestBeing.getSkills().keySet().stream()
			.forEach(requestSkillCode -> {

				Integer requestSkillValue = requestBeing.getSkills().get(requestSkillCode);
				
				MudBeingSkill skill = 
					dbBeing.getSkills().stream()
						.filter(d -> d.getId().getCode().equals(requestSkillCode))
						.findFirst()
						.orElse(MudBeingSkillConverter.build(
								dbBeing.getCode(), requestSkillCode, requestSkillValue));
				
				
				// Update the skill value
				skill.setValue(requestSkillValue);
				
				dbBeing.getSkills().add(skill);
				
			});
		
		return dbBeing;
	}

}
