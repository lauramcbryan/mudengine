package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;

public class MudBeingAttrModifierConverter {

	private MudBeingAttrModifierConverter() {
		
	}
	
	public static MudBeingAttrModifier convert(Long beingCode, BeingAttrModifier attrModifier) {
		
		MudBeingAttrModifier newDbAttrModifier = new MudBeingAttrModifier();
		MudBeingAttrModifierPK newDbAttrModifierPK = new MudBeingAttrModifierPK();
		
		newDbAttrModifierPK.setAttrCode(attrModifier.getAttrCode());
		newDbAttrModifierPK.setBeingCode(beingCode);
		newDbAttrModifierPK.setOriginCode(attrModifier.getOriginCode());
		newDbAttrModifierPK.setOriginType(attrModifier.getOriginType());
		
		newDbAttrModifier.setId(newDbAttrModifierPK);
		newDbAttrModifier.setOffset(attrModifier.getOffset());
		newDbAttrModifier.setEndTurn(attrModifier.getEndTurn());

		return newDbAttrModifier;
	}
	
	public static MudBeing sync(MudBeing dbBeing, Being requestBeing) {
		
		if (requestBeing.getAttrModifiers()!=null) {
			
			// Looking for attrModifiers to delete
			dbBeing.getAttrModifiers().removeIf(d -> 
				
				requestBeing.getAttrModifiers().stream()
					.noneMatch(e -> d.getId().getAttrCode().equals(e.getAttrCode()))
			);
			
			// Looking for attrModifiers to add/update
			requestBeing.getAttrModifiers().stream()
				.forEach(requestAttr -> {
					
					// Retrieve the existing modifier in database record
					MudBeingAttrModifier attrModifier =
						dbBeing.getAttrModifiers().stream()
							.filter(e -> e.getId().getAttrCode().equals(requestAttr.getAttrCode()))
							.findFirst()
							.orElse(MudBeingAttrModifierConverter.convert(dbBeing.getBeingCode(), requestAttr));
					
					// Updating the modifier regardless it was found in current list or just created
					attrModifier.setOffset(requestAttr.getOffset());
					
					dbBeing.getAttrModifiers().add(attrModifier);
				});
		}
				
		return dbBeing;
	}
	
	public static MudBeing sync(MudBeing dbBeing, MudBeingClass previousClass, MudBeingClass nextClass) {
		
		if (previousClass!=null) {
			
			// Looking for attrModifiers to delete
			dbBeing.getAttrModifiers().removeIf(d -> {
				
				boolean existsInOldClass = 
						previousClass.getAttributes().stream()
						.anyMatch(e -> d.getId().getAttrCode().equals(e.getId().getAttrCode()));
				
				boolean existsInNewClass =
						nextClass.getAttributes().stream()
						.anyMatch(e -> d.getId().getAttrCode().equals(e.getId().getAttrCode()));
				
				return existsInOldClass && !existsInNewClass;
			});
		}
		
		return dbBeing;
		
	}
}
