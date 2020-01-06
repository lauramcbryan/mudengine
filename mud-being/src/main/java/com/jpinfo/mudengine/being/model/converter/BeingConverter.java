package com.jpinfo.mudengine.being.model.converter;

import java.util.stream.Collectors;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.MudBeingSkill;
import com.jpinfo.mudengine.common.being.Being;

public class BeingConverter {

	private BeingConverter() {	}
	
	public static Being convert(MudBeing dbBeing) {

		Being response = new Being();

		response.setType(Being.enumBeingType.values()[dbBeing.getType()]);
		
		response.setCode(dbBeing.getCode());
		response.setPlayerId(dbBeing.getPlayerId());
		response.setCurPlaceCode(dbBeing.getCurPlaceCode());
		response.setCurWorld(dbBeing.getCurWorld());
		response.setQuantity(dbBeing.getQuantity());
		
		if (dbBeing.getName()!=null) {
			response.setName(dbBeing.getName());
		} else {
			response.setName(dbBeing.getBeingClass().getName());
		}
		
		response.setBeingClass(
				BeingClassConverter.convert(dbBeing.getBeingClass()));
		
		response.setBaseAttrs(
				dbBeing.getAttrs().stream()
				.collect(Collectors.toMap(MudBeingAttr::getCode, MudBeingAttr::getValue))
				);
		
		response.setBaseSkills(
			dbBeing.getSkills().stream()
				.collect(Collectors.toMap(MudBeingSkill::getCode, MudBeingSkill::getValue))
				);

		response.setSkillModifiers( 
			dbBeing.getSkillModifiers().stream()
				.map(BeingSkillModifierConverter::convert)
				.collect(Collectors.toList())
				);
		
		response.setAttrModifiers(
				dbBeing.getAttrModifiers().stream()
				.map(BeingAttrModifierConverter::convert)
				.collect(Collectors.toList())
				);

		return response;
	}
}
