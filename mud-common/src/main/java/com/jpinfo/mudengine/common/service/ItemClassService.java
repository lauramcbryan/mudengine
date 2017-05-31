package com.jpinfo.mudengine.common.service;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jpinfo.mudengine.common.itemClass.ItemClass;

@RequestMapping("/item/class")
public interface ItemClassService {

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	ItemClass getItemClass(@PathVariable("id") String id);

}