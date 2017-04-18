package com.jpinfo.mudengine.being.utils;

import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingItem;
import com.jpinfo.mudengine.being.model.MudBeingSkill;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingItemPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillPK;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;
import com.jpinfo.mudengine.common.being.BeingItem;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;

public class BeingHelper {

	
	public static Being buildBeing(MudBeing dbBeing) {
		
		Being response = new Being();
		
		response.setBeingCode(dbBeing.getBeingCode());
		response.setBeingClass(dbBeing.getBeingClass());
		response.setName(dbBeing.getName());
		response.setPlayerId(dbBeing.getPlayerId());
		response.setCurPlaceCode(dbBeing.getCurPlaceCode());
		response.setCurWorld(dbBeing.getCurWorld());
		
		for(MudBeingAttr curAttr: dbBeing.getAttributes()) {
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
		
		if (requestBeing.getAttrs()!=null) {
			
			List<MudBeingAttr> attrList = new ArrayList<MudBeingAttr>();
			
			for(String curAttr: requestBeing.getAttrs().keySet()) {
				
				MudBeingAttr dbAttr = new MudBeingAttr();
				MudBeingAttrPK dbAttrPK = new MudBeingAttrPK();
				
				dbAttrPK.setAttrCode(curAttr);
				dbAttrPK.setBeingCode(dbBeing.getBeingCode());
				
				dbAttr.setId(dbAttrPK);
				dbAttr.setValue(requestBeing.getAttrs().get(curAttr));
				
				attrList.add(dbAttr);
			}
			
			dbBeing.getAttributes().clear();
			dbBeing.getAttributes().addAll(attrList);
		}
		
		return dbBeing;
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

	public static MudBeing updateBeingItems(MudBeing dbBeing, Being requestBeing) {
		
		if (requestBeing.getItems()!=null) {
			
			List<MudBeingItem> itemList = new ArrayList<MudBeingItem>();
			
			for(Integer curItemCode: requestBeing.getItems().keySet()) {
				
				BeingItem curItem = requestBeing.getItems().get(curItemCode);
				
				MudBeingItem newDbItem = new MudBeingItem();
				MudBeingItemPK newDbItemPK = new MudBeingItemPK();
				
				newDbItemPK.setBeingCode(dbBeing.getBeingCode());
				newDbItemPK.setItemCode(curItemCode);
				
				newDbItem.setId(newDbItemPK);
				newDbItem.setQtty(curItem.getQtty());
				newDbItem.setUsageCount(curItem.getUsageCount());
				
				itemList.add(newDbItem);
			}

			dbBeing.getItems().clear();
			dbBeing.getItems().addAll(itemList);
		}

		return dbBeing;
	}
	
	

}
