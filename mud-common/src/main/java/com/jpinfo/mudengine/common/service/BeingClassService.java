package com.jpinfo.mudengine.common.service;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jpinfo.mudengine.common.being.BeingClass;

@RequestMapping("/being/class")
public interface BeingClassService {

	@GetMapping(value="{id}")
	BeingClass getClass(@PathVariable("id") String id);
	
	@GetMapping(value="")
	List<BeingClass> listAllAvailable();

}