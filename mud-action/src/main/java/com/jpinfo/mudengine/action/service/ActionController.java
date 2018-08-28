package com.jpinfo.mudengine.action.service;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClassCommand;
import com.jpinfo.mudengine.action.model.converter.ActionConverter;
import com.jpinfo.mudengine.action.model.converter.MudActionConverter;
import com.jpinfo.mudengine.action.repository.MudActionClassCommandRepository;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.service.ActionService;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@RestController
@RequestMapping("/action")
public class ActionController implements ActionService {

	@Autowired
	private MudActionRepository repository;
		
	@Autowired
	private MudActionClassCommandRepository commandRepository;
	
	@Autowired
	private ActionHandler handler;

	@Override
	public Iterable<Action> getActiveActions(@PathVariable Long actorCode) {
		
		List<MudAction> stateList = repository.findByIssuerCode(actorCode);

		return stateList.stream()
			.map(ActionConverter::convert)
			.collect(Collectors.toList());
	}

	@Override
	public Action getAction(@PathVariable Long actionCode) {
		
		MudAction state = repository.findById(actionCode)
				.orElseThrow(() -> new EntityNotFoundException("Action not found"));
		
		return ActionConverter.convert(state);
	}	
	
	@Override
	public Action insertCommand( 
			@PathVariable("commandId") Integer commandId,
			@RequestParam("mediatorCode") Optional<String> mediatorCode, 
			@RequestParam("targetCode") String targetCode)
	{
		
		MudActionClassCommand command = commandRepository.findById(commandId)
				.orElseThrow(() -> new EntityNotFoundException("Command not recognized"));
		
		MudUserDetails uDetails = (MudUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		Session sessionData = uDetails.getSessionData()
				.orElseThrow(() -> new IllegalArgumentException(LocalizedMessages.SESSION_NOT_FOUND));
		
		MudAction dbAction = MudActionConverter.build(command, 
				sessionData.getCurWorldName(), sessionData.getBeingCode(), mediatorCode, targetCode);
		
		// Save the new command; obtain an actionId
		dbAction = repository.save(dbAction);
		
		return ActionConverter.convert(dbAction);
	}
	
	@Override
	public void cancelAction(@PathVariable Long actionCode) {
		
		MudAction dbAction = repository.findActiveOne(actionCode);
		
		if (dbAction!=null) {
			
			dbAction.setCurrStateEnum(Action.EnumActionState.CANCELLED);
			
			repository.save(dbAction);
		}
	}

	@Override
	public void cancelAllActionFromBeing(@PathVariable Long actorCode) {
		
		List<MudAction> dbActionList = repository.findActiveByActorCode(actorCode);
		
		for(MudAction curAction: dbActionList) {
			
			curAction.setCurrStateEnum(Action.EnumActionState.CANCELLED);
			
			repository.save(curAction);
		}
	}
	
	@Override
	public void updateActions() {
		
		// Get the current turn
		Long currentTurn = getCurrentTurn();

		// Get the list of running actions and update them.
		handler.runActions(currentTurn, repository.findRunningActions(currentTurn));
		
		// Find and start not started actions		
		handler.runActions(currentTurn, repository.findPendingActions());
	}
	
	private Long getCurrentTurn() {
		return System.currentTimeMillis();
	}
}
