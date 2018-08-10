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
		
			response.setCode(dbItem.getCode());
			response.setCurPlaceCode(dbItem.getCurPlaceCode());
			response.setCurWorld(dbItem.getCurWorld());
			response.setQuantity(dbItem.getQuantity());
			response.setCurOwner(dbItem.getCurOwner());
			
			response.setName(dbItem.getName()!=null ? 
						dbItem.getName() : 
						dbItem.getItemClass().getName());
			
			response.setItemClass(ItemClassConverter.convert(dbItem.getItemClass()));
			
			response.setAttrs(
					dbItem.getAttrs().stream()
					.collect(Collectors.toMap(
							MudItemAttr::getCode, 
							MudItemAttr::getValue))
					);
		}
		
		return response;
	}
	
}
