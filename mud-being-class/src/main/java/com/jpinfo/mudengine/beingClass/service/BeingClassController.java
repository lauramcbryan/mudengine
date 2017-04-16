package com.jpinfo.mudengine.beingClass.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.beingClass.model.MudBeingClass;
import com.jpinfo.mudengine.beingClass.repository.BeingClassRepository;
import com.jpinfo.mudengine.beingClass.util.BeingClassHelper;
import com.jpinfo.mudengine.common.beingClass.BeingClass;

@RestController
@RequestMapping("/being/class")
public class BeingClassController {

	@Autowired
	private BeingClassRepository repository;
	
	@RequestMapping(method=RequestMethod.GET, value="{id}")
	public BeingClass getClass(@PathVariable String id) {
		
		MudBeingClass found = repository.findOne(id);
		
		BeingClass result = BeingClassHelper.buildBeingClass(found);
		
	
		return result;
	}

}
