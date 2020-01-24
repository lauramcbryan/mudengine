package com.jpinfo.mudengine.compose.dto;

import java.util.List;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

import lombok.Builder;

@Builder
public class PlaceComposite {

	private Place place;
	
	private List<Item> items;
	
	private List<Being> beings;
}
