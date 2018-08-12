package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;

public class BeingAttrModifierConverter {

	private BeingAttrModifierConverter()  { }
	
	public static BeingAttrModifier convert(MudBeingAttrModifier dbModifier) {
		
		BeingAttrModifier result = new BeingAttrModifier();
		
		result.setCode(dbModifier.getId().getCode());
		result.setOriginCode(dbModifier.getId().getOriginCode());
		result.setOriginType(dbModifier.getId().getOriginType());
		result.setEndTurn(dbModifier.getEndTurn());
		
		return result;
	}
}
