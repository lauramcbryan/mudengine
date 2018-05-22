package com.jpinfo.mudengine.common.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jpinfo.mudengine.common.being.BeingClass;

@RequestMapping("/being/class")
public interface BeingClassService {

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	BeingClass getClass(@PathVariable("id") String id);
	
	@RequestMapping(method=RequestMethod.GET, value="")
	List<BeingClass> listAllAvailable();

}