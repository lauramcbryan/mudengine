package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.placeclass.PlaceClass;
import com.jpinfo.mudengine.common.service.PlaceClassService;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.converter.PlaceClassConverter;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;

@RestController
public class PlaceClassController implements PlaceClassService {

	@Autowired
	private PlaceClassRepository repository;
	
	@Override
	public PlaceClass getPlaceClass(@PathVariable String placeClass) {
		
		MudPlaceClass found = 
				repository.findById(placeClass)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLACE_CLASS_NOT_FOUND));
		
		return PlaceClassConverter.convert(found);
	}
}
