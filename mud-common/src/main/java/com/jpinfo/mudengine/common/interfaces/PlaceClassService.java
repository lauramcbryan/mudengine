package com.jpinfo.mudengine.common.interfaces;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jpinfo.mudengine.common.placeClass.PlaceClass;

@RequestMapping("/place/class")
public interface PlaceClassService {

	@RequestMapping(method=RequestMethod.GET, value="/{placeClass}")
	PlaceClass getPlaceClass(@PathVariable("placeClass") String placeClass);

}