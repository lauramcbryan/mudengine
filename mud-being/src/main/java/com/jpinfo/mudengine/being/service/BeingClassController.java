package com.jpinfo.mudengine.being.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.repository.BeingClassRepository;
import com.jpinfo.mudengine.being.utils.BeingClassHelper;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.service.BeingClassService;

@RestController
public class BeingClassController implements BeingClassService {

	@Autowired
	private BeingClassRepository repository;
	
	@Override
	public BeingClass getClass(@PathVariable String id) {
		
		MudBeingClass found = repository
				.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Item class entity not found"));
		
		BeingClass result = BeingClassHelper.buildBeingClass(found);
		
	
		return result;
	}

}
