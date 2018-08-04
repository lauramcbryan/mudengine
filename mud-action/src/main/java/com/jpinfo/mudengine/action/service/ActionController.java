package com.jpinfo.mudengine.action.service;

import java.util.ArrayList;





import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.model.MudActionClassCommand;
import com.jpinfo.mudengine.action.repository.MudActionClassCommandRepository;
import com.jpinfo.mudengine.action.repository.MudActionClassRepository;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.action.utils.ActionTestResult;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.service.ActionService;

@RestController
@RequestMapping("/action")
public class ActionController implements ActionService {

	@Autowired
	private MudActionRepository repository;
		
	@Autowired
	private MudActionClassCommandRepository commandRepository;
	
	@Autowired
	private MudActionClassRepository classRepository;
	
	@Autowired
	private ActionHandler handler;

	@Override
	public Iterable<Action> getActiveActions(@PathVariable Long actorCode) {
		
		List<Action> responseList = new ArrayList<>();
		
		List<MudAction> stateList = repository.findByIssuerCode(actorCode);
		
		
		for(MudAction curState: stateList) {
			
			responseList.add(ActionHelper.buildAction(curState));
		}
		
		return responseList;
	}

	@Override
	public Action getAction(@PathVariable Long actionCode) {
		
		Action response = null;
		
		MudAction state = repository.findById(actionCode)
				.orElseThrow(() -> new EntityNotFoundException("Action not found"));
		
		if (state!=null) {
			response = ActionHelper.buildAction(state);
		}
		
		return response;
	}	
	
	@Override
	public Action insertCommand( 
			@PathVariable("commandId") Integer commandId,
			@RequestParam("actorCode") Long actorCode,
			@RequestParam("mediatorCode") Optional<String> mediatorCode, 
			@RequestParam("targetCode") String targetCode)
	{
		
		Action response = null;
		
		MudAction dbAction = new MudAction();
		
		MudActionClassCommand command = commandRepository.findById(commandId)
				.orElseThrow(() -> new EntityNotFoundException("Command not recognized"));
		
		MudActionClass actionClass = 
				classRepository.findById(command.getActionClassCode())
					.orElseThrow(() -> new EntityNotFoundException("Action class not recognized"));

		
		dbAction.setActorCode(actorCode);
		dbAction.setIssuerCode(actorCode);
		dbAction.setActionClassCode(command.getActionClassCode());
		dbAction.setMediatorType(actionClass.getMediatorType());
		dbAction.setTargetType(actionClass.getTargetType());
		
		
		if (mediatorCode.isPresent())
			dbAction.setMediatorCode(mediatorCode.get());
		
		dbAction.setTargetCode(targetCode);
		dbAction.setCurrStateEnum(Action.EnumActionState.NOT_STARTED);
		
		
		// Save the new command; obtain an actionId
		dbAction = repository.save(dbAction);
		
		response = ActionHelper.buildAction(dbAction);
		
		return response;
	}
	
	
	@RequestMapping(value="/test/{commandId}", method=RequestMethod.GET)
	public ActionTestResult testExpression(
			@PathVariable("commandId") Integer commandId, 
			@RequestParam("actorCode") Long actorCode, 
			@RequestParam("mediatorCode") Optional<String> mediatorCode, 
			@RequestParam("targetCode") String targetCode,
			@RequestParam("expression") Optional<String> expression) {
		
		ActionTestResult result = new ActionTestResult();
		
		// mount an Action
		Action action = new Action();

		MudActionClassCommand command = commandRepository.findById(commandId)
				.orElseThrow(() -> new EntityNotFoundException("Command not recognized"));

		MudActionClass actionClass = 
				classRepository.findById(command.getActionClassCode())
					.orElseThrow(() -> new EntityNotFoundException("Action class not recognized"));
		
		
		action.setActorCode(actorCode);
		action.setIssuerCode(actorCode);
		action.setActionClassCode(command.getActionClassCode());
		action.setMediatorType(Action.EnumTargetType.valueOf(actionClass.getMediatorType()));
		action.setTargetType(Action.EnumTargetType.valueOf(actionClass.getTargetType()));
		
		
		if (mediatorCode.isPresent())
			action.setMediatorCode(mediatorCode.get());
		
		action.setTargetCode(targetCode);
		action.setCurState(Action.EnumActionState.NOT_STARTED);
		
		// mount an ActionInfo
		result.setTestData(handler.buildActionInfo(action));
		
		if (expression.isPresent()) {
			
			try {
				ExpressionParser parser = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext(result.getTestData());
				context.setVariable("action", result.getTestData());

				// Running successRate expressions
				Expression curExpression = parser.parseExpression(expression.get());

				result.setResult(curExpression.getValue(context));
			} catch(Exception e) {
				result.setResult(e);
			}
			
		}
			
		
		return result;
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
	
}
