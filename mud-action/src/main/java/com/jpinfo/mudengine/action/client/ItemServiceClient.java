package com.jpinfo.mudengine.action.client;

import java.util.List;

import com.jpinfo.mudengine.common.item.Item;

public interface ItemServiceClient {

	Item getItem(Long itemId);
	
	Item updateItem(Long itemId, Item item);
	
	List<Item> getAllFromBeing(Long owner);
	
	List<Item> getAllFromPlace(String worldName, Integer placeCode);
}
