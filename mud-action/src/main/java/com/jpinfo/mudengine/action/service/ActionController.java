package com.jpinfo.mudengine.action.service;

import java.util.ArrayList;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.repository.MudActionClassRepository;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionHandler;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.action.utils.ActionTestResult;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.service.ActionService;

@RestController
@RequestMapping("/action")
public class ActionController implements ActionService {

	@Autowired
	private MudActionRepository repository;
	
	@Autowired
	private MudActionClassRepository classRepository;
	
	@Autowired
	private ActionHandler handler;

	@Override
	public Iterable<Action> getActiveActions(@RequestHeader String authToken, @PathVariable Long actorCode) {
		
		List<Action> responseList = new ArrayList<Action>();
		
		List<MudAction> stateList = repository.findByIssuerCode(actorCode);
		
		
		for(MudAction curState: stateList) {
			
			responseList.add(ActionHelper.buildAction(curState));
		}
		
		return responseList;
	}

	@Override
	public Action getAction(@RequestHeader String authToken, @PathVariable Long actionCode) {
		
		Action response = null;
		
		MudAction state = repository.findOne(actionCode);
		
		if (state!=null) {
			response = ActionHelper.buildAction(state);
		}
		
		return response;
	}	
	
	@Override
	public Action insertCommand(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@PathVariable("verb") String verb, @RequestParam("actorCode") Long actorCode, 
			@RequestParam("targetCode") String targetCode, @RequestParam("targetType") String targetType)
	{
		
		Action response = null;
		
		MudAction dbAction = new MudAction();
		
		MudActionClass dbActionClass = classRepository.findByVerb(verb);
		
		if (dbActionClass!=null) {
			dbAction.setActorCode(actorCode);
			dbAction.setIssuerCode(actorCode);
			dbAction.setActionClassCode(dbActionClass.getActionClassCode());
			dbAction.setMediatorCode(null);
			dbAction.setTargetCode(targetCode);
			dbAction.setTargetType(Action.EnumTargetType.valueOf(targetType));
			dbAction.setCurrState(Action.EnumActionState.NOT_STARTED);
			
			// Save the new command; obtain an actionId
			dbAction = repository.save(dbAction);
			
			response = ActionHelper.buildAction(dbAction);
		} else {
			throw new EntityNotFoundException("Verb " + verb + " not recognized.");
		}
		
		
		return response;
	}
	
	
	@RequestMapping(value="/test/{verb}", method=RequestMethod.GET)
	public ActionTestResult testExpression(@PathVariable("verb") String verb, 
			@RequestParam("actorCode") Long actorCode, 
			@RequestParam("targetCode") String targetCode, 
			@RequestParam("targetType") String targetType,
			@RequestParam(value="expression", required=false) String expression) {
		
		ActionTestResult result = new ActionTestResult();
		
		// mount an Action
		Action action = new Action();
		
		MudActionClass dbActionClass = classRepository.findByVerb(verb);
		
		if (dbActionClass!=null) {
		
			action.setIssuerCode(actorCode);
			action.setActorCode(actorCode);
			action.setTargetCode(targetCode);
			action.setTargetType(Action.EnumTargetType.valueOf(targetType));
			action.setActionClassCode(dbActionClass.getActionClassCode());
			
			action.setTargetType(Action.EnumTargetType.valueOf(targetType));
			action.setCurState(Action.EnumActionState.NOT_STARTED);
			
			// mount an ActionInfo
			result.setTestData(handler.buildAction(action));
			
			if (expression!=null) {
				
				try {
					ExpressionParser parser = new SpelExpressionParser();
					EvaluationContext context = new StandardEvaluationContext(result.getTestData());
					context.setVariable("action", result.getTestData());
	
					// Running successRate expressions
					Expression curExpression = parser.parseExpression(expression);
	
					result.setResult(curExpression.getValue(context));
				} catch(Exception e) {
					result.setResult(e);
				}
				
			}
			
		} else {
			throw new EntityNotFoundException("Verb " + verb + " not recognized.");
		}
		
		return result;
	}
	
	@Override
	public void cancelAction(@RequestHeader String authToken, @PathVariable Long actionCode) {
		
		MudAction dbAction = repository.findActiveOne(actionCode);
		
		if (dbAction!=null) {
			
			dbAction.setCurrState(Action.EnumActionState.CANCELLED);
			
			repository.save(dbAction);
		}
	}

	@Override
	public void cancelAllActionFromBeing(@RequestHeader String authToken, @PathVariable Long actorCode) {
		
		List<MudAction> dbActionList = repository.findActiveByActorCode(actorCode);
		
		for(MudAction curAction: dbActionList) {
			
			curAction.setCurrState(Action.EnumActionState.CANCELLED);
			
			repository.save(curAction);
		}
	}

	@Override
	public void cancelAllActionFromPlace(@RequestHeader String authToken, @PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<MudAction> dbActionList = repository.findActiveByPlace(worldName, placeCode);
		
		for(MudAction curAction: dbActionList) {
			
			curAction.setCurrState(Action.EnumActionState.CANCELLED);
			
			repository.save(curAction);
		}
	}
	
}
