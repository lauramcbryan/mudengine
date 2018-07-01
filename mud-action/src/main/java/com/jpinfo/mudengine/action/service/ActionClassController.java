package com.jpinfo.mudengine.action.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.action.model.MudActionClassCommand;
import com.jpinfo.mudengine.action.repository.MudActionClassCommandRepository;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.service.ActionClassService;

@RestController
public class ActionClassController implements ActionClassService {

	@Autowired
	private MudActionClassCommandRepository commandRepository;
	
	@Override
	public List<Command> getAvailableCommands(@PathVariable("locale") String locale) {
		
		List<Command> resultList = new ArrayList<Command>();
		
		List<MudActionClassCommand> lstCommands = 
				commandRepository.findByLocale(locale);
		
		lstCommands.stream().forEach(d -> {
			
			resultList.add(ActionHelper.buildCommand(d));
			
		});
		
		return resultList;
	}

}
