package com.jpinfo.mudengine.action.model.converter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.dto.BeingComposite;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

@Component
public class BeingCompositeConverter {
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private PlaceServiceClient placeService;
	
	@Autowired
	private ItemServiceClient itemService;
	
	public BeingComposite build(Long beingCode) {
		
		Being being = beingService.getBeing(beingCode);
		Place curPlace = placeService.getPlace(being.getCurPlaceCode());
		List<Item> actorItems = itemService.getAllFromBeing(beingCode);
		
		return new BeingComposite(being, actorItems, curPlace);
	}
}
