package com.jpinfo.mudengine.action.client;

import com.jpinfo.mudengine.common.item.Item;

public interface ItemServiceClient {

	Item getItem(Long itemId);
	
	Item updateItem(Long itemId, Item item);
	
}
