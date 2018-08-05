package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.common.being.BeingClass;

public class BeingClassConverter {

	private BeingClassConverter() { }
	
	public static BeingClass convert(MudBeingClass a) {
		
		BeingClass result = new BeingClass();
		
		result.setBeingClassCode(a.getBeingClassCode());
		result.setName(a.getName());
		result.setDescription(a.getDescription());
		result.setSize(a.getSize());
		result.setWeightCapacity(a.getWeightCapacity());
		
		for(MudBeingClassAttr curAttr: a.getAttributes()) {
			result.getAttributes().put(curAttr.getId().getAttrCode(), curAttr.getAttrValue());
		}
		
		for(MudBeingClassSkill curSkill: a.getSkills()) {
			result.getSkills().put(curSkill.getId().getSkillCode(), curSkill.getSkillValue());
			
		}
		
		return result;
	}
	
}
