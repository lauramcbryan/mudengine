package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.world.model.BeingClass;
import com.jpinfo.mudengine.world.repository.BeingClassRepository;

@RestController
@RequestMapping("/class")
public class BeingClassController {

	@Autowired
	private BeingClassRepository repository;
	
	@RequestMapping(method=RequestMethod.GET, value="{id}")
	public BeingClass getClass(@PathVariable String id) {
		
		BeingClass found = repository.findOne(id);
	
		return found;
	}

}
