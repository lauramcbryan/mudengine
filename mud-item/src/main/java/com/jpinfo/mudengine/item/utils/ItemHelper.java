package com.jpinfo.mudengine.item.utils;

import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.itemClass.ItemClass;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemAttr;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.model.MudItemClassAttr;
import com.jpinfo.mudengine.item.model.pk.MudItemAttrPK;

public class ItemHelper {
	
	private ItemHelper() { }

	public static Item buildItem(MudItem dbItem) {
		
		Item response = new Item();
		
		response.setItemCode(dbItem.getItemCode());
		response.setItemClassCode(dbItem.getItemClass().getItemClass());
		response.setCurPlaceCode(dbItem.getCurPlaceCode());
		response.setCurWorld(dbItem.getCurWorld());
		response.setQuantity(dbItem.getQuantity());
		response.setCurOwner(dbItem.getCurOwner());
		
		response.setItemClass(ItemHelper.buildItemClass(dbItem.getItemClass()));
		
		for(MudItemAttr curAttr: dbItem.getAttrs()) {
			response.getAttrs().put(curAttr.getId().getAttrCode(), curAttr.getAttrValue());
		}
		
		return response;
	}
	
	public static ItemClass buildItemClass(MudItemClass a) {
		
		ItemClass result = new ItemClass();
		
		result.setItemClass(a.getItemClass());
		result.setSize(a.getSize());
		result.setWeight(a.getWeight());
		result.setDescription(a.getDescription());
		
		for(MudItemClassAttr curAttr: a.getAttrs()) {
			result.getAttrs().put(curAttr.getId().getAttrCode(), curAttr.getAttrValue());
		}
		
		return result;
	}
	
	public static MudItemAttr buildMudItemAttr(Long itemCode, MudItemClassAttr classAttr) {
		
		MudItemAttr response = new MudItemAttr();
		MudItemAttrPK pk = new MudItemAttrPK();
		
		pk.setAttrCode(classAttr.getId().getAttrCode());
		pk.setItemCode(itemCode);
		
		response.setId(pk);
		response.setAttrValue(classAttr.getAttrValue());
		
		return response;
	}
	
	public static MudItemAttr buildMudItemAttr(Long itemCode, String attrCode, Integer attrValue) {
		
		
		MudItemAttr response = new MudItemAttr();
		MudItemAttrPK pk = new MudItemAttrPK();
		
		pk.setAttrCode(attrCode);
		pk.setItemCode(itemCode);
		
		response.setId(pk);
		response.setAttrValue(attrValue);
		
		return response;
	}
	

	public static MudItem changeItemAttrs(MudItem dbItem, MudItemClass previousItemClass, MudItemClass itemClass) {
		
		// If a previous itemClass exist, remove the attributes set by it
		if (previousItemClass!=null) {
			
			for(MudItemClassAttr curClassAttr: previousItemClass.getAttrs()) {
				
				MudItemAttr oldAttr = ItemHelper.buildMudItemAttr(dbItem.getItemCode(), curClassAttr);
				
				dbItem.getAttrs().remove(oldAttr);
			}
			
		}

		// Adding attributes for the new itemClass
		for(MudItemClassAttr curClassAttr: itemClass.getAttrs()) {
			
			MudItemAttr newAttr = ItemHelper.buildMudItemAttr(dbItem.getItemCode(), curClassAttr);
			
			dbItem.getAttrs().add(newAttr);
		}

		return dbItem;
	}

}
