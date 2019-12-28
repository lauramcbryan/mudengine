package com.jpinfo.mudengine.action.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.action.service.ActionClassServiceImpl;
import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.service.ActionClassService;

@RestController
public class ActionClassController implements ActionClassService {

	@Autowired
	private ActionClassServiceImpl service;
	
	@Override
	public List<Command> getAvailableCommands(@PathVariable("locale") String locale) {
		
		return service.getAvailableCommands(locale);
	}

}
