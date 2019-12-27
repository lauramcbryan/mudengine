package com.jpinfo.mudengine.common.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jpinfo.mudengine.common.placeclass.PlaceClass;

@RequestMapping("/place/class")
public interface PlaceClassService {

	@GetMapping(value="/{placeClass}")
	PlaceClass getPlaceClass(@PathVariable("placeClass") String placeClass);

}