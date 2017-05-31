package com.jpinfo.mudengine.item.utils;

import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.itemClass.ItemClass;
import com.jpinfo.mudengine.item.model.MudItemClassSkill;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemAttr;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.model.MudItemClassAttr;
import com.jpinfo.mudengine.item.model.MudItemSkill;
import com.jpinfo.mudengine.item.model.pk.MudItemAttrPK;
import com.jpinfo.mudengine.item.model.pk.MudItemSkillPK;

public class ItemHelper {

	public static Item buildItem(MudItem dbItem) {
		
		Item response = new Item();
		
		response.setItemCode(dbItem.getItemCode());
		response.setItemClass(dbItem.getItemClass());
		response.setName(dbItem.getName());
		response.setDescription(dbItem.getDescription());
		response.setUsageCount(dbItem.getUsageCount());
		
		for(MudItemAttr curAttr: dbItem.getAttrs()) {
			response.getAttrModifiers().put(curAttr.getId().getAttrCode(), curAttr.getOffset());
		}
		
		for(MudItemSkill curSkill: dbItem.getSkills()) {
			response.getSkillModifiers().put(curSkill.getId().getSkillCode(), curSkill.getOffset());
		}
		
		return response;
	}
	
	public static MudItem updateItemSkills(MudItem dbItem, Item requestItem) {
		
		if (requestItem.getSkillModifiers()!=null) {
			
			List<MudItemSkill> skillList = new ArrayList<MudItemSkill>();
			
			for(String curSkill: requestItem.getSkillModifiers().keySet()) {

				MudItemSkill newSkill = new MudItemSkill();
				MudItemSkillPK pk = new MudItemSkillPK();
				
				pk.setSkillCode(curSkill);
				pk.setItemCode(dbItem.getItemCode());
				
				newSkill.setId(pk);
				newSkill.setOffset(requestItem.getSkillModifiers().get(curSkill));
				
				skillList.add(newSkill);
			}

			dbItem.getSkills().clear();
			dbItem.getSkills().addAll(skillList);
		}
		
		return dbItem;
	}
	
	public static MudItem updateItemAttrs(MudItem dbItem, Item requestItem) {
		
		if (requestItem.getAttrModifiers()!=null) {
			
			List<MudItemAttr> attrList = new ArrayList<MudItemAttr>();
			
			for(String curAttribute: requestItem.getAttrModifiers().keySet()) {
				
				MudItemAttr newAttr = new MudItemAttr();
				MudItemAttrPK pk = new MudItemAttrPK(); 
				
				pk.setAttrCode(curAttribute);
				pk.setItemCode(dbItem.getItemCode());
			
				newAttr.setId(pk);
				newAttr.setOffset(requestItem.getAttrModifiers().get(curAttribute));
				
				attrList.add(newAttr);
			}
			
			dbItem.getAttrs().clear();
			dbItem.getAttrs().addAll(attrList);
		}
		
		return dbItem;
	}
	
	
	public static ItemClass buildItemClass(MudItemClass a) {
		
		ItemClass result = new ItemClass();
		
		result.setItemClass(a.getItemClass());
		result.setDurability(a.getDurability());
		result.setSize(a.getSize());
		result.setWeight(a.getWeight());
		
		for(MudItemClassAttr curAttr: a.getAttrs()) {
			result.getAttrModifiers().put(curAttr.getId().getAttrCode(), curAttr.getOffset());
		}
		
		for(MudItemClassSkill curSkill: a.getSkills()) {
			result.getSkillModifiers().put(curSkill.getId().getSkillCode(), curSkill.getOffset());
		}
		
		return result;
	}

}
