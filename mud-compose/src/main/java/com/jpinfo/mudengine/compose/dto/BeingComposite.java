package com.jpinfo.mudengine.compose.dto;

import java.util.List;
import java.util.Map;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;

import lombok.Builder;

@Builder
public class BeingComposite {

	private Being being;
	
	private List<Item> inventory;
	
	private Map<String, Item> equipment;
}
