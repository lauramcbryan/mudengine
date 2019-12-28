package com.jpinfo.mudengine.action.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.action.model.MudActionClassCommand;
import com.jpinfo.mudengine.action.model.converter.CommandConverter;
import com.jpinfo.mudengine.action.repository.MudActionClassCommandRepository;
import com.jpinfo.mudengine.common.action.Command;

@Service
public class ActionClassServiceImpl {

	@Autowired
	private MudActionClassCommandRepository commandRepository;
	
	public List<Command> getAvailableCommands(String locale) {
		
		List<MudActionClassCommand> lstCommands = 
				commandRepository.findByLocale(locale);
		
		return lstCommands.stream()
			.map(CommandConverter::convert)
			.collect(Collectors.toList());
		
	}

}
