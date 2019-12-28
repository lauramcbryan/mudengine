package com.jpinfo.mudengine.action.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.action.service.ActionServiceImpl;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.service.ActionService;

@RestController
@RequestMapping("/action")
public class ActionController implements ActionService {

	@Autowired
	private ActionServiceImpl service;

	@Override
	public Iterable<Action> getActiveActions(@PathVariable Long actorCode) {
		
		return service.getActiveActions(actorCode);
	}

	@Override
	public Action getAction(@PathVariable Long actionCode) {
		
		return service.getAction(actionCode);
	}	
	
	@Override
	public Action insertCommand( 
			@PathVariable("commandId") Integer commandId,
			@RequestParam("mediatorCode") Optional<String> mediatorCode, 
			@RequestParam("targetCode") String targetCode)
	{
		
		return service.insertCommand(commandId, mediatorCode, targetCode);
	}
	
	@Override
	public void cancelAction(@PathVariable Long actionCode) {
		
		service.cancelAction(actionCode);
	}

	@Override
	public void cancelAllActionFromBeing(@PathVariable Long actorCode) {
		
		service.cancelAllActionFromBeing(actorCode);
	}
	
	@Override
	public void updateActions() {
		
		service.updateActions();
	}
}
