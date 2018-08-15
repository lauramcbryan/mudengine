package com.jpinfo.mudengine.being.client;

import com.jpinfo.mudengine.common.item.Item;

public interface ItemServiceClient  {
	
	Item getItem(Long itemId);

	void dropAllFromBeing(Long owner, String worldName, Integer placeCode);
}
