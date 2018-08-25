package com.jpinfo.mudengine.action.model.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.dto.ItemComposite;
import com.jpinfo.mudengine.common.item.Item;

@Component
public class ItemCompositeConverter {
	
	@Autowired
	private ItemServiceClient itemService;

	public ItemComposite build(Long itemCode) {
		
		Item item = itemService.getItem(itemCode);
		
		return new ItemComposite(item);
	}
}
