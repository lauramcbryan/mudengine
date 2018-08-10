package com.jpinfo.mudengine.item.model.converter;

import java.util.stream.Collectors;

import com.jpinfo.mudengine.common.itemclass.ItemClass;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.model.MudItemClassAttr;

public class ItemClassConverter {
	
	private ItemClassConverter() { }

	public static ItemClass convert(MudItemClass dbItemClass) {
		
		ItemClass result = new ItemClass();
		
		result.setCode(dbItemClass.getCode());
		result.setName(dbItemClass.getName());
		result.setSize(dbItemClass.getSize());
		result.setWeight(dbItemClass.getWeight());
		result.setDescription(dbItemClass.getDescription());
		
		result.setAttrs(
				dbItemClass.getAttrs().stream()
				.collect(Collectors.toMap(
						MudItemClassAttr::getCode, 
						MudItemClassAttr::getValue))
			);
		
		return result;
	}
}
