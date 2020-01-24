package com.jpinfo.mudengine.compose.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.compose.client.BeingServiceClient;
import com.jpinfo.mudengine.compose.client.ItemServiceClient;
import com.jpinfo.mudengine.compose.client.PlaceServiceClient;
import com.jpinfo.mudengine.compose.dto.PlaceComposite;

@Service
public class ComposeService {

	@Autowired
	private PlaceServiceClient placeService;
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private ItemServiceClient itemService;
	
	public PlaceComposite buildPlaceComposite(String worldName, Integer placeCode) {
		
		return PlaceComposite.builder()
				.place(placeService.getPlace(placeCode))
				.beings(beingService.getAllFromPlace(worldName, placeCode))
				.items(itemService.getAllFromPlace(worldName, placeCode))
				.build();
	}
}
