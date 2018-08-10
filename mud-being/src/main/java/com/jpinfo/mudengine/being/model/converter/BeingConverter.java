package com.jpinfo.mudengine.being.model.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingSkill;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;

public class BeingConverter {

	private BeingConverter() {	}
	
	public static Being convert(MudBeing dbBeing) {
		return convert(dbBeing, false);
	}

	public static Being convert(MudBeing dbBeing, boolean fullResponse) {

		Being response = new Being();

		response.setType(Being.enumBeingType.values()[dbBeing.getType()]);
		response.setCode(dbBeing.getCode());
		response.setName(dbBeing.getName());
		response.setPlayerId(dbBeing.getPlayerId());
		response.setCurPlaceCode(dbBeing.getCurPlaceCode());
		response.setCurWorld(dbBeing.getCurWorld());
		response.setQuantity(dbBeing.getQuantity());

		response.setBeingClass(BeingClassConverter.convert(dbBeing.getBeingClass()));

		for (MudBeingAttr curAttr : dbBeing.getAttrs()) {

			// Calculating the attribute effective value
			int effectiveAttrValue = calcEffectiveAttr(curAttr.getId().getCode(),
					curAttr.getValue(), dbBeing);

			response.getAttrs().put(curAttr.getId().getCode(), effectiveAttrValue);
		}

		for (MudBeingSkill curSkill : dbBeing.getSkills()) {

			int effectiveSkillValue = calcEffectiveSkill(curSkill.getId().getSkillCode(),
					curSkill.getSkillValue(), dbBeing);

			response.getSkills().put(curSkill.getId().getSkillCode(), effectiveSkillValue);
		}

		if (fullResponse) {

			Map<String, Integer> baseAttrMap = new HashMap<>();
			Map<String, Integer> baseSkillMap = new HashMap<>();
			List<BeingAttrModifier> attrModifierList = new ArrayList<>();
			List<BeingSkillModifier> skillModifierList = new ArrayList<>();

			for (MudBeingAttr curAttr : dbBeing.getAttrs()) {
				baseAttrMap.put(curAttr.getId().getCode(), curAttr.getValue());
			}

			for (MudBeingSkill curSkill : dbBeing.getSkills()) {
				baseSkillMap.put(curSkill.getId().getSkillCode(), curSkill.getSkillValue());
			}

			for (MudBeingSkillModifier curSkillModifier : dbBeing.getSkillModifiers()) {
				BeingSkillModifier dummy = new BeingSkillModifier();

				dummy.setCode(curSkillModifier.getId().getCode());
				dummy.setOriginCode(curSkillModifier.getId().getOriginCode());
				dummy.setOriginType(curSkillModifier.getId().getOriginType());

				dummy.setOffset(curSkillModifier.getOffset());
				dummy.setEndTurn(curSkillModifier.getEndTurn());

				skillModifierList.add(dummy);
			}

			for (MudBeingAttrModifier curAttrModifier : dbBeing.getAttrModifiers()) {
				BeingAttrModifier dummy = new BeingAttrModifier();

				dummy.setCode(curAttrModifier.getId().getCode());
				dummy.setOriginCode(curAttrModifier.getId().getOriginCode());
				dummy.setOriginType(curAttrModifier.getId().getOriginType());

				dummy.setOffset(curAttrModifier.getOffset());
				dummy.setEndTurn(curAttrModifier.getEndTurn());

				attrModifierList.add(dummy);
			}

			response.setBaseAttrs(baseAttrMap);
			response.setBaseSkills(baseSkillMap);
			response.setAttrModifiers(attrModifierList);
			response.setSkillModifiers(skillModifierList);
		}

		return response;
	}

	private static int calcEffectiveAttr(String attrCode, Integer baseValue, MudBeing dbBeing) {

		// Base value for attribute
		float response = baseValue;

		// Traverse all modifier list
		for (MudBeingAttrModifier curAttrModifier : dbBeing.getAttrModifiers()) {

			if (curAttrModifier.getId().getCode().equals(attrCode)) {

				response += curAttrModifier.getOffset();
			}
		}

		return Math.round(response);
	}

	private static int calcEffectiveSkill(String skillCode, Integer baseValue, MudBeing dbBeing) {

		float response = baseValue;

		for (MudBeingSkillModifier curSkillModifier : dbBeing.getSkillModifiers()) {

			if (curSkillModifier.getId().getCode().equals(skillCode)) {
				response += curSkillModifier.getOffset();
			}
		}

		return Math.round(response);

	}
}
