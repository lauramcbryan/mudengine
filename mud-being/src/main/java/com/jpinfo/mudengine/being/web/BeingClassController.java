package com.jpinfo.mudengine.being.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.being.service.BeingClassServiceImpl;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.service.BeingClassService;

@RestController
public class BeingClassController implements BeingClassService {

	@Autowired
	private BeingClassServiceImpl service;
	
	@Override
	public BeingClass getClass(@PathVariable String id) {
		
		return service.getClass(id);
	}

	@Override
	public List<BeingClass> listAllAvailable() {
		
		return service.listAllAvailable();
	}

}
