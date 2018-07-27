package com.jpinfo.mudengine.world.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.placeclass.PlaceClass;
import com.jpinfo.mudengine.common.service.PlaceClassService;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;
import com.jpinfo.mudengine.world.util.WorldHelper;

@RestController
public class PlaceClassController implements PlaceClassService {

	@Autowired
	private PlaceClassRepository repository;
	
	@Override
	public PlaceClass getPlaceClass(@PathVariable String placeClass) {
		
		PlaceClass result = null;
		
		Optional<MudPlaceClass> found = repository.findById(placeClass);
		
		if (found.isPresent()) {
			result = WorldHelper.buildPlaceClass(found.get());
		} else {
			throw new EntityNotFoundException("Place class not found");
		}
		
		return result;
	}
}
