package com.jpinfo.mudengine.beingClass.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.beingClass.model.MudBeingClass;
import com.jpinfo.mudengine.beingClass.repository.BeingClassRepository;
import com.jpinfo.mudengine.beingClass.util.BeingClassHelper;
import com.jpinfo.mudengine.common.beingClass.BeingClass;
import com.jpinfo.mudengine.common.interfaces.BeingClassService;

@RestController
public class BeingClassController implements BeingClassService {

	@Autowired
	private BeingClassRepository repository;
	
	@Override
	public BeingClass getClass(@PathVariable String id) {
		
		MudBeingClass found = repository.findOne(id);
		
		BeingClass result = BeingClassHelper.buildBeingClass(found);
		
	
		return result;
	}

}
