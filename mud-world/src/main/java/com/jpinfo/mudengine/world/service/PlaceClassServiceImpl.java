package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.placeclass.PlaceClass;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.converter.PlaceClassConverter;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;

@Service
public class PlaceClassServiceImpl {

	@Autowired
	private PlaceClassRepository repository;
	
	public PlaceClass getPlaceClass(String placeClass) {
		
		MudPlaceClass found = 
				repository.findById(placeClass)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLACE_CLASS_NOT_FOUND));
		
		return PlaceClassConverter.convert(found);
	}
}
