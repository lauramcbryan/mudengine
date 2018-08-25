package com.jpinfo.mudengine.action.model.converter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.dto.PlaceComposite;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

@Component
public class PlaceCompositeConverter {
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private PlaceServiceClient placeService;
	
	@Autowired
	private ItemServiceClient itemService;

	public PlaceComposite build(String worldName, Integer placeCode) {

		Place place = placeService.getPlace(placeCode);
		List<Being> beings = beingService.getAllFromPlace(worldName, placeCode);
		List<Item> items = itemService.getAllFromPlace(worldName, placeCode);
		
		return new PlaceComposite(place, items, beings);

	}
}
