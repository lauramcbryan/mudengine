package com.jpinfo.mudengine.world.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.placeclass.PlaceClass;
import com.jpinfo.mudengine.common.service.PlaceClassService;
import com.jpinfo.mudengine.world.service.PlaceClassServiceImpl;

@RestController
public class PlaceClassController implements PlaceClassService {

	@Autowired
	private PlaceClassServiceImpl service;
	
	@Override
	public PlaceClass getPlaceClass(@PathVariable String placeClass) {
		
		return service.getPlaceClass(placeClass);
	}
}
