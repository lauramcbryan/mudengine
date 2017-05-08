package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.interfaces.PlaceClassService;
import com.jpinfo.mudengine.common.placeClass.PlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;
import com.jpinfo.mudengine.world.util.WorldHelper;

@RestController
public class PlaceClassController implements PlaceClassService {

	@Autowired
	private PlaceClassRepository repository;
	
	@Override
	public PlaceClass getPlaceClass(@PathVariable String placeClass) {
		
		MudPlaceClass found = repository.findOne(placeClass);
		
		PlaceClass result = WorldHelper.buildPlaceClass(found);
		
		
		return result;
	}
}
