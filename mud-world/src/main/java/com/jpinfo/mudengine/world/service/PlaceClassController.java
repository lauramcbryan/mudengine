package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.placeClass.PlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;
import com.jpinfo.mudengine.world.util.WorldHelper;

@RestController
@RequestMapping("/place/class")
public class PlaceClassController {

	@Autowired
	private PlaceClassRepository repository;
	
	@RequestMapping(method=RequestMethod.GET, value="/{placeClass}")
	public PlaceClass getPlaceClass(@PathVariable String placeClass) {
		
		MudPlaceClass found = repository.findOne(placeClass);
		
		PlaceClass result = WorldHelper.buildPlaceClass(found);
		
		
		return result;
	}
}
