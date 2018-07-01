package com.jpinfo.mudengine.common.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.action.Command;

@RestController
@RequestMapping("/action/class")
public interface ActionClassService {

	
	@RequestMapping(path="/commands/{locale}", method=RequestMethod.GET)
	public List<Command> getAvailableCommands(@PathVariable("locale") String locale);
}
