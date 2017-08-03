package com.jpinfo.mudengine.being.utils;

import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.being.model.MudBeingClassSlot;
import com.jpinfo.mudengine.being.model.MudBeingSkill;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.MudBeingSlot;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSlotPK;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;

public class BeingHelper {

	
	public static Being buildBeing(MudBeing dbBeing) {
		
		Being response = new Being();
		
		response.setBeingCode(dbBeing.getBeingCode());
		response.setBeingClass(dbBeing.getBeingClass().getBeingClassCode());
		response.setName(dbBeing.getName());
		response.setPlayerId(dbBeing.getPlayerId());
		response.setCurPlaceCode(dbBeing.getCurPlaceCode());
		response.setCurWorld(dbBeing.getCurWorld());
		
		for(MudBeingAttr curAttr: dbBeing.getAttrs()) {
			response.getAttrs().put(curAttr.getId().getAttrCode(), curAttr.getValue());
		}
		
		for(MudBeingSkill curSkill: dbBeing.getSkills()) {
			response.getSkills().put(curSkill.getId().getSkillCode(), curSkill.getValue());
		}
		
		for(MudBeingSkillModifier curSkillModifier: dbBeing.getSkillModifiers()) {
			BeingSkillModifier dummy = new BeingSkillModifier();
			
			dummy.setSkillCode(curSkillModifier.getId().getSkillCode());
			dummy.setOriginCode(curSkillModifier.getId().getOriginCode());
			dummy.setOriginType(curSkillModifier.getId().getOriginType());
			
			dummy.setOffset(curSkillModifier.getOffset());
			dummy.setEndTurn(curSkillModifier.getEndTurn());
			
			response.getSkillModifiers().add(dummy);
		}
		
		for(MudBeingAttrModifier curAttrModifier: dbBeing.getAttrModifiers()) {
			BeingAttrModifier dummy = new BeingAttrModifier();
			
			dummy.setAttribute(curAttrModifier.getId().getAttrCode());
			dummy.setOriginCode(curAttrModifier.getId().getOriginCode());
			dummy.setOriginType(curAttrModifier.getId().getOriginType());
			
			dummy.setOffset(curAttrModifier.getOffset());
			dummy.setEndTurn(curAttrModifier.getEndTurn());
			
			response.getAttrModifiers().add(dummy);
		}
		
		return response;
	}
	
	public static MudBeing updateBeingAttributes(MudBeing dbBeing, Being requestBeing) {

		// Looking for attributes to remove
		for(MudBeingAttr curItemAttr: dbBeing.getAttrs()) {
			
			// If it not exists in request, remove it
			if (requestBeing.getAttrs().get(curItemAttr.getId().getAttrCode())==null) {
				dbBeing.getAttrs().remove(curItemAttr);
			}
		}

		// Looking for attributes to add
		for(String curAttr: requestBeing.getAttrs().keySet()) {
			
			MudBeingAttr newAttr = BeingHelper.buildMudBeingAttr(dbBeing.getBeingCode(), 
					curAttr, 
					requestBeing.getAttrs().get(curAttr));
			
			if (!dbBeing.getAttrs().contains(newAttr)) {
				dbBeing.getAttrs().add(newAttr);
			}
		}
		
		return dbBeing;
	}
	
	public static MudBeing updateBeingClass(MudBeing dbBeing, MudBeingClass previousClass, MudBeingClass beingClass) {
		
		if (previousClass!=null) {
			
			// Removing attributes set by previous being class
			// (attributes modifiers aren't changed)
			for (MudBeingClassAttr curAttr: previousClass.getAttributes()) {
				
				MudBeingAttr oldAttr = BeingHelper.buildMudBeingAttr(dbBeing.getBeingCode(), curAttr.getId().getAttrCode(), curAttr.getAttrValue());
				
				dbBeing.getAttrs().remove(oldAttr);
			}
			
			// Removing skills set by previous being class
			// (skills modifiers aren't changed)
			for (MudBeingClassSkill curSkill: previousClass.getSkills()) {
				
				MudBeingSkill oldSkill = BeingHelper.buildMudBeingSkill(dbBeing.getBeingCode(), curSkill.getId().getSkillCode(), curSkill.getSkillValue());
				
				dbBeing.getSkills().remove(oldSkill);
			}
			
			// Removing slots set by previous being class
			for (MudBeingClassSlot curSlot: previousClass.getSlots()) {
				
				MudBeingSlot oldSlot = BeingHelper.buildMudBeingSlot(dbBeing.getBeingCode(), curSlot.getId().getSlotCode());
				
				dbBeing.getEquipment().remove(oldSlot);
			}
			
		}
		
		// Adding attributes from new beingClass
		for(MudBeingClassAttr curAttr: beingClass.getAttributes()) {
			
			MudBeingAttr newAttr = BeingHelper.buildMudBeingAttr(dbBeing.getBeingCode(), curAttr.getId().getAttrCode(), curAttr.getAttrValue());
			
			if (!dbBeing.getAttrs().contains(newAttr)) {
				dbBeing.getAttrs().add(newAttr);
			}
		}
		
		// Adding skills from new beingClass
		for (MudBeingClassSkill curSkill: beingClass.getSkills()) {
			
			MudBeingSkill newSkill = BeingHelper.buildMudBeingSkill(dbBeing.getBeingCode(), curSkill.getId().getSkillCode(), curSkill.getSkillValue());
			
			if (!dbBeing.getSkills().contains(newSkill)) {
				dbBeing.getSkills().add(newSkill);
			}
		}
		
		// Adding slots from new beingClass
		for (MudBeingClassSlot curSlot: beingClass.getSlots()) {
		
			MudBeingSlot newSlot = BeingHelper.buildMudBeingSlot(dbBeing.getBeingCode(), curSlot.getId().getSlotCode());
			
			if (!dbBeing.getEquipment().contains(newSlot)) {
				dbBeing.getEquipment().add(newSlot);
			}
		}
		
		return dbBeing;
	}
	
	public static MudBeingAttr buildMudBeingAttr(Long beingCode, String attrCode, Integer attrValue) {
		
		MudBeingAttr dbAttr = new MudBeingAttr();
		MudBeingAttrPK dbAttrPK = new MudBeingAttrPK();
		
		dbAttrPK.setAttrCode(attrCode);
		dbAttrPK.setBeingCode(beingCode);
		
		dbAttr.setId(dbAttrPK);
		dbAttr.setValue(attrValue);
		
		return dbAttr;
	}
	
	public static MudBeingSkill buildMudBeingSkill(Long beingCode, String skillCode, Integer skillValue) {
		
		MudBeingSkill dbSkill = new MudBeingSkill();
		MudBeingSkillPK dbSkillPK = new MudBeingSkillPK();
		
		dbSkillPK.setSkillCode(skillCode);
		dbSkillPK.setBeingCode(beingCode);
		
		dbSkill.setId(dbSkillPK);
		dbSkill.setValue(skillValue);
		
		return dbSkill;
	}
	
	public static MudBeingSlot buildMudBeingSlot(Long beingCode, String slotCode) {
		
		MudBeingSlot dbSlot = new MudBeingSlot();
		MudBeingSlotPK dbSlotPK = new MudBeingSlotPK();
		
		dbSlotPK.setBeingCode(beingCode);
		dbSlotPK.setSlotCode(slotCode);
		
		dbSlot.setId(dbSlotPK);
		
		return dbSlot;
	}
	
	
	public static MudBeing updateBeingSkills(MudBeing dbBeing, Being requestBeing) {
		
		if (requestBeing.getSkills()!=null) {
			
			List<MudBeingSkill> skillsList = new ArrayList<MudBeingSkill>();
			
			for(String curSkill: requestBeing.getSkills().keySet()) {
				
				MudBeingSkill newSkill = new MudBeingSkill();
				MudBeingSkillPK newSkillPK = new MudBeingSkillPK();
				
				newSkillPK.setSkillCode(curSkill);
				newSkillPK.setBeingCode(dbBeing.getBeingCode());
				
				newSkill.setId(newSkillPK);
				newSkill.setValue(requestBeing.getSkills().get(curSkill));
				
				skillsList.add(newSkill);
			}

			dbBeing.getSkills().clear();
			dbBeing.getSkills().addAll(skillsList);
		}
		
		return dbBeing;
	}

	public static MudBeing updateBeingAttrModifiers(MudBeing dbBeing, Being requestBeing) {
		
		if (requestBeing.getAttrModifiers()!=null) {
			
			List<MudBeingAttrModifier> attrList = new ArrayList<MudBeingAttrModifier>();
			
			for(BeingAttrModifier curAttrModifier: requestBeing.getAttrModifiers()) {
				
				MudBeingAttrModifier newDbAttrModifier = new MudBeingAttrModifier();
				MudBeingAttrModifierPK newDbAttrModifierPK = new MudBeingAttrModifierPK();
				
				newDbAttrModifierPK.setAttrCode(curAttrModifier.getAttribute());
				newDbAttrModifierPK.setBeingCode(dbBeing.getBeingCode());
				newDbAttrModifierPK.setOriginCode(curAttrModifier.getOriginCode());
				newDbAttrModifierPK.setOriginType(curAttrModifier.getOriginType());
				
				newDbAttrModifier.setId(newDbAttrModifierPK);
				newDbAttrModifier.setOffset(curAttrModifier.getOffset());
				newDbAttrModifier.setEndTurn(curAttrModifier.getEndTurn());
				
				attrList.add(newDbAttrModifier);
			}

			dbBeing.getAttrModifiers().clear();
			dbBeing.getAttrModifiers().addAll(attrList);
		}
				
		return dbBeing;
	}
	
	
	public static MudBeing updateBeingSkillModifiers(MudBeing dbBeing, Being requestBeing) {
		
		if (requestBeing.getSkillModifiers()!=null) {
			
			List<MudBeingSkillModifier> skillList = new ArrayList<MudBeingSkillModifier>();
			
			for(BeingSkillModifier curSkillModifier: requestBeing.getSkillModifiers()) {
				
				MudBeingSkillModifier newDbSkillModifier = new MudBeingSkillModifier();
				MudBeingSkillModifierPK newDbSkillModifierPK = new MudBeingSkillModifierPK();
				
				newDbSkillModifierPK.setBeingCode(dbBeing.getBeingCode());
				newDbSkillModifierPK.setSkillCode(curSkillModifier.getSkillCode());
				newDbSkillModifierPK.setOriginCode(curSkillModifier.getOriginCode());
				newDbSkillModifierPK.setOriginType(curSkillModifier.getOriginType());
				
				newDbSkillModifier.setId(newDbSkillModifierPK);
				newDbSkillModifier.setOffset(curSkillModifier.getOffset());
				newDbSkillModifier.setEndTurn(curSkillModifier.getEndTurn());
				
				skillList.add(newDbSkillModifier);
			}

			dbBeing.getSkillModifiers().clear();
			dbBeing.getSkillModifiers().addAll(skillList);
		}
		
		return dbBeing;
	}
}
