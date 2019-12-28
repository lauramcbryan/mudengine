package com.jpinfo.mudengine.world.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.service.PlaceService;
import com.jpinfo.mudengine.world.service.PlaceServiceImpl;

import io.swagger.annotations.ApiOperation;

@RestController
public class PlaceController implements PlaceService {
	
	@Autowired
	private PlaceServiceImpl service;
	
	@Override
	@ApiOperation(value="Returns information about a place")
	public Place getPlace(
			@PathVariable Integer placeId) {

		return service.getPlace(placeId);
	}

	
	@Override
	public Place updatePlace(@PathVariable Integer placeId, @RequestBody Place requestPlace) {
		
		return service.updatePlace(placeId, requestPlace);
	}

	@Override
	public void destroyPlace(@PathVariable Integer placeId) {
		
		service.destroyPlace(placeId);
	}


	@Override
	public ResponseEntity<Place> createPlace(String placeClassCode, String direction, Integer targetPlaceCode) {
		
		Place response = service.createPlace(placeClassCode, direction, targetPlaceCode);
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
