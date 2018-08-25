package com.jpinfo.mudengine.action.model.converter;

import java.util.TreeSet;
import java.util.stream.Collectors;

import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.common.action.ActionClass;

public class ActionClassConverter {

	private ActionClassConverter() { }
	
	public static ActionClass convert(MudActionClass dbClass) {
		
		ActionClass result = new ActionClass();
		
		result.setActionClassCode(dbClass.getActionClassCode());
		result.setActionType(dbClass.getActionType());
		result.setNroTurnsExpr(dbClass.getNroTurnsExpr());
		result.setSuccessRateExpr(dbClass.getSuccessRateExpr());

		result.setPrereqList(
			dbClass.getPrereqList().stream()
				.map(ActionClassPrereqConverter::convert)
				.collect(Collectors.toCollection(TreeSet::new))
				);

		result.setEffectList(
			dbClass.getEffectList().stream()
				.map(ActionClassEffectConverter::convert)
				.collect(Collectors.toCollection(TreeSet::new))
				);
		
		return result;
	}
}
