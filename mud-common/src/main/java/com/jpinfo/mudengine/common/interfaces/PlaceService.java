package com.jpinfo.mudengine.common.interfaces;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jpinfo.mudengine.common.place.Place;

@RequestMapping("/place")
public interface PlaceService {

	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	Place getPlace(@PathVariable("id") Integer id);

	@RequestMapping(method=RequestMethod.POST, value="/{id}")
	void updatePlace(@PathVariable("id") Integer id, @RequestBody Place requestPlace);

}