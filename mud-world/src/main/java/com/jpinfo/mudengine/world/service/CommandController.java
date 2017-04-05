package com.jpinfo.mudengine.world.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/command")
public class CommandController {
	
	@RequestMapping(method=RequestMethod.POST)
	public void insertCommand() {
		
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public void getAvailableCommands() {
		
	}

}
