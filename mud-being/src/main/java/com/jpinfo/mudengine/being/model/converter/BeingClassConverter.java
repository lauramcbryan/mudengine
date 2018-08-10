package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.common.being.BeingClass;

public class BeingClassConverter {

	private BeingClassConverter() { }
	
	public static BeingClass convert(MudBeingClass a) {
		
		BeingClass result = new BeingClass();
		
		result.setCode(a.getCode());
		result.setName(a.getName());
		result.setDescription(a.getDescription());
		result.setSize(a.getSize());
		result.setWeightCapacity(a.getWeightCapacity());
		
		for(MudBeingClassAttr curAttr: a.getAttrs()) {
			result.getAttrs().put(curAttr.getId().getCode(), curAttr.getValue());
		}
		
		for(MudBeingClassSkill curSkill: a.getSkills()) {
			result.getSkills().put(curSkill.getId().getCode(), curSkill.getValue());
			
		}
		
		return result;
	}
	
}
