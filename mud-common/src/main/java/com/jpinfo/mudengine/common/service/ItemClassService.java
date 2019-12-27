package com.jpinfo.mudengine.common.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;

import com.jpinfo.mudengine.common.itemclass.ItemClass;

@RequestMapping("/item/class")
public interface ItemClassService {

	@GetMapping(value="{id}")
	ItemClass getItemClass(@PathVariable("id") String id);

}