package com.jpinfo.mudengine.being.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.being.model.Being;
import com.jpinfo.mudengine.being.repository.BeingRepository;

@RestController
@RequestMapping("/being")
public class BeingController {
	
	@Autowired
	private BeingRepository repository;

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	public Being getBeing(@PathVariable Integer id) {
		
		Being found = repository.findOne(id);
		
		return found;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public Iterable<Being> getAllBeings() {
		
		Iterable<Being> result = repository.findAll();
		
		return result;
	}
}
