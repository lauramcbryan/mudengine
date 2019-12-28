package com.jpinfo.mudengine.action.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClassCommand;
import com.jpinfo.mudengine.action.model.MudActionTurn;
import com.jpinfo.mudengine.action.model.converter.ActionConverter;
import com.jpinfo.mudengine.action.model.converter.MudActionConverter;
import com.jpinfo.mudengine.action.repository.MudActionClassCommandRepository;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.repository.MudActionTurnRepository;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@Service
public class ActionServiceImpl {

	@Autowired
	private MudActionRepository repository;
		
	@Autowired
	private MudActionClassCommandRepository commandRepository;
	
	@Autowired
	private MudActionTurnRepository turnRepository;
	
	@Autowired
	private ActionHandler handler;

	public Iterable<Action> getActiveActions(Long actorCode) {
		
		List<MudAction> stateList = repository.findByIssuerCode(actorCode);

		return stateList.stream()
			.map(ActionConverter::convert)
			.collect(Collectors.toList());
	}

	public Action getAction(Long actionCode) {
		
		MudAction state = repository.findById(actionCode)
				.orElseThrow(() -> new EntityNotFoundException("Action not found"));
		
		return ActionConverter.convert(state);
	}	
	
	public Action insertCommand(Integer commandId,Optional<String> mediatorCode, String targetCode)
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
	
	public void cancelAction(Long actionCode) {
		
		MudAction dbAction = repository.findActiveOne(actionCode);
		
		if (dbAction!=null) {
			
			dbAction.setCurrStateEnum(Action.EnumActionState.CANCELLED);
			
			repository.save(dbAction);
		}
	}

	public void cancelAllActionFromBeing(Long actorCode) {
		
		List<MudAction> dbActionList = repository.findActiveByActorCode(actorCode);
		
		for(MudAction curAction: dbActionList) {
			
			curAction.setCurrStateEnum(Action.EnumActionState.CANCELLED);
			
			repository.save(curAction);
		}
	}
	
	public void updateActions() {

		// Prepare the new turn
		MudActionTurn currentTurn = new MudActionTurn();
		
		// set start processing time
		currentTurn.setStartedAt(LocalDate.now());
		
		// Saves in database (will generate the turn number from sequence)
		currentTurn = turnRepository.save(currentTurn);

		// Get the list of running actions and update them.
		handler.runActions(currentTurn.getNroTurn(), repository.findRunningActions(currentTurn.getNroTurn()));
		
		// Find and start not started actions		
		handler.runActions(currentTurn.getNroTurn(), repository.findPendingActions());
		
		// Set end processing time
		currentTurn.setFinishedAt(LocalDate.now());
		
		// Update in database
		turnRepository.save(currentTurn);
	}
}
