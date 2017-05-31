package com.jpinfo.mudengine.beingClass.util;

import com.jpinfo.mudengine.beingClass.model.MudBeingClass;
import com.jpinfo.mudengine.beingClass.model.MudBeingClassAttr;
import com.jpinfo.mudengine.beingClass.model.MudBeingClassSkill;
import com.jpinfo.mudengine.common.beingClass.BeingClass;

public class BeingClassHelper {

	public static BeingClass buildBeingClass(MudBeingClass a) {
		
		BeingClass result = new BeingClass();
		
		result.setBeingClass(a.getBeingClass());
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
