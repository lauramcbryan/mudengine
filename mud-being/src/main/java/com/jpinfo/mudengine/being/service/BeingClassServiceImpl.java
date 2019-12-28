package com.jpinfo.mudengine.being.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.converter.BeingClassConverter;
import com.jpinfo.mudengine.being.repository.BeingClassRepository;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@Service
public class BeingClassServiceImpl {

	@Autowired
	private BeingClassRepository repository;
	
	public BeingClass getClass(String id) {
		
		MudBeingClass found = repository
				.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_CLASS_NOT_FOUND));
		
	
		return BeingClassConverter.convert(found);
	}

	public List<BeingClass> listAllAvailable() {
		
		List<BeingClass> resultList = new ArrayList<>();
		
		Iterable<MudBeingClass> dbList = repository.findAll();
		
		dbList.forEach(d -> 
			resultList.add(BeingClassConverter.convert(d))
		);
		
		return resultList;
	}

}
