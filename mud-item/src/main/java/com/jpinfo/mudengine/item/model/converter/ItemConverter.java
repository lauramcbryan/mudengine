package com.jpinfo.mudengine.item.model.converter;

import java.util.stream.Collectors;

import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemAttr;

public class ItemConverter {

	private ItemConverter() {}
	
	public static Item convert(MudItem dbItem) {
		
		Item response = new Item();
		
		if (dbItem!=null) {
		
			response.setItemCode(dbItem.getItemCode());
			response.setItemClassCode(dbItem.getItemClass().getItemClassCode());
			response.setCurPlaceCode(dbItem.getCurPlaceCode());
			response.setCurWorld(dbItem.getCurWorld());
			response.setQuantity(dbItem.getQuantity());
			response.setCurOwner(dbItem.getCurOwner());
			
			response.setItemName(dbItem.getItemName()!=null ? 
						dbItem.getItemName() : 
						dbItem.getItemClass().getItemClassName());
			
			response.setItemClass(ItemClassConverter.convert(dbItem.getItemClass()));
			
			response.setAttrs(
					dbItem.getAttrs().stream()
					.collect(Collectors.toMap(
							MudItemAttr::getAttrCode, 
							MudItemAttr::getAttrValue))
					);
		}
		
		return response;
	}
	
}
