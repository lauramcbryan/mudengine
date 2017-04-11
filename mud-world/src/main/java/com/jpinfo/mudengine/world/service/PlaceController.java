package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.world.model.Place;
import com.jpinfo.mudengine.world.repository.PlaceRepository;

@RestController
@RequestMapping("/place")
public class PlaceController {
	
	@Autowired
	private PlaceRepository placeRepository;

	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public Place getPlace(@PathVariable Integer id) {
		
		Place found = placeRepository.findOne(id);
		
		return found;
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/{id}")
	public void updatePlace(@PathVariable Integer id, @RequestBody Place place) {
		
		Place found = placeRepository.findOne(id);

		// O que se permite atualizar:

		// beings		
		found.setBeings(place.getBeings());
		
		// items		
		found.setItems(place.getItems());
		
		// exits		
		found.setExits(place.getExits());
		
		placeRepository.save(found);
		
	}
}
