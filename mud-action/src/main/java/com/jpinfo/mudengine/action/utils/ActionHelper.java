package com.jpinfo.mudengine.action.utils;

import java.util.ArrayList;
import java.util.Arrays;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.model.MudActionClassCommand;
import com.jpinfo.mudengine.action.model.MudActionClassEffect;
import com.jpinfo.mudengine.action.model.MudActionClassPrereq;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionClass;
import com.jpinfo.mudengine.common.action.ActionClassEffect;
import com.jpinfo.mudengine.common.action.ActionClassPrereq;
import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.action.CommandParam;
import com.jpinfo.mudengine.common.action.Action.EnumTargetType;

public class ActionHelper {
	
	public static Action buildAction(MudAction a) {
		
		Action state = new Action();
		
		state.setActionId(a.getActionId());
		state.setStartTurn(a.getStartTurn());
		state.setEndTurn(a.getEndTurn());
		state.setCurState(a.getCurrStateEnum());
		state.setActionClassCode(a.getActionClassCode());
		state.setActorCode(a.getActorCode());
		
		state.setMediatorCode(a.getMediatorCode());
		
		if (a.getMediatorType()!=null)
			state.setMediatorType(EnumTargetType.valueOf(a.getMediatorType()));
		
		state.setTargetCode(a.getTargetCode());
		state.setTargetType(a.getTargetTypeEnum());
		
		return state;
	}
	
	public static ActionClass buildActionClass(MudActionClass a) {
	
		ActionClass result = new ActionClass();
		
		result.setActionClassCode(a.getActionClassCode());
		result.setActionType(a.getActionType());
		result.setNroTurnsExpr(a.getNroTurnsExpr());
		result.setSuccessRateExpr(a.getSuccessRateExpr());
		
		for(MudActionClassPrereq curPrereq: a.getPrereqList()) {
			
			ActionClassPrereq newPrereq = new ActionClassPrereq();
			
			newPrereq.setEvalOrder(curPrereq.getEvalOrder());
			newPrereq.setCheckExpression(curPrereq.getCheckExpression());
			newPrereq.setFailExpression(curPrereq.getFailExpression());
			
			result.getPrereqList().add(newPrereq);
		}

		for(MudActionClassEffect curEffect: a.getEffectList()) {
			
			ActionClassEffect newEffect = new ActionClassEffect();
			
			newEffect.setEvalOrder(curEffect.getEvalOrder());
			newEffect.setExpression(curEffect.getExpression());
			newEffect.setMessageExpression(curEffect.getMessageExpression());
			
			result.getEffectList().add(newEffect);
		}
		
		return result;
	}
	
	public static Command buildCommand(MudActionClassCommand dbCommand) {
		
		Command result = new Command();
		
		result.setCommandId(dbCommand.getCommandId());
		result.setCategory(Command.enumCategory.GAME);
		result.setDescription(dbCommand.getDescription());
		result.setLogged(true);
		result.setUsage(dbCommand.getUsage());
		result.setVerb(dbCommand.getVerb());
		
		result.setParameters(new ArrayList<CommandParam>());
		
		dbCommand.getParameterList().forEach(d -> {
			
			CommandParam newParam = new CommandParam();
			
			newParam.setName(d.getPk().getName());
			newParam.setRequired(((Integer)1).equals(d.getRequired()));
			newParam.setType(CommandParam.enumParamTypes.valueOf(d.getType()));
			newParam.setDefaultValue(d.getDefaultValue());
			
			if (d.getDomainValues()!=null) {
				
				String[] arrDomainValues = d.getDomainValues().split(", ");
				
				newParam.setDomainValues(
						Arrays.asList(arrDomainValues)
					);
			}
			
			newParam.setInputMessage(d.getInputMessage());
			
			result.getParameters().add(newParam);
			
		});
		
		return result;
	}
}
