package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeing;

import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrPK;
import com.jpinfo.mudengine.common.being.Being;

public class MudBeingAttrConverter {

	private MudBeingAttrConverter() { }
	
	public static MudBeingAttr build(Long beingCode, String attrCode, Integer attrValue) {
		
		MudBeingAttr dbAttr = new MudBeingAttr();
		MudBeingAttrPK dbAttrPK = new MudBeingAttrPK();
		
		dbAttrPK.setAttrCode(attrCode);
		dbAttrPK.setBeingCode(beingCode);
		
		dbAttr.setId(dbAttrPK);
		dbAttr.setAttrValue(attrValue);
		
		return dbAttr;
	}
	
	public static MudBeingAttr convert(Long beingCode, MudBeingClassAttr classAttr) {
		
		MudBeingAttr dbAttr = new MudBeingAttr();
		MudBeingAttrPK dbAttrPK = new MudBeingAttrPK();
		
		dbAttrPK.setAttrCode(classAttr.getId().getAttrCode());
		dbAttrPK.setBeingCode(beingCode);
		
		dbAttr.setId(dbAttrPK);
		dbAttr.setAttrValue(classAttr.getAttrValue());
		
		return dbAttr;
	}
	
	
	
	public static MudBeing sync(MudBeing dbBeing, MudBeingClass previousClass, MudBeingClass nextClass) {

		if (previousClass!=null) {
		
			// Looking for attributes to remove
			dbBeing.getAttrs().removeIf(d -> {
				
				boolean existsInOldClass = previousClass.getAttributes().stream()
						.anyMatch(e -> d.getId().getAttrCode().equals(e.getId().getAttrCode()));
				
				boolean existsInNewClass = nextClass.getAttributes().stream()
						.anyMatch(e -> d.getId().getAttrCode().equals(e.getId().getAttrCode()));
				
				return existsInOldClass && ! existsInNewClass;
			});
		}
		
		// Looking for attributes to add/update
		nextClass.getAttributes().stream()
			.forEach(d -> {

				MudBeingAttr attr = 
					dbBeing.getAttrs().stream()
						.filter(e -> e.getId().getAttrCode().equals(d.getId().getAttrCode()))
						.findFirst()
						.orElse(MudBeingAttrConverter.convert(dbBeing.getBeingCode(), d));

				// Update the attribute value
				attr.setAttrValue(d.getAttrValue());
				
				dbBeing.getAttrs().add(attr);
				
			});
		
		return dbBeing;
	}
	
	public static MudBeing sync(MudBeing dbBeing, Being requestBeing) {

		// Looking for attributes to remove
		dbBeing.getAttrs().removeIf(d -> 
			!requestBeing.getAttrs().containsKey(d.getId().getAttrCode())
		);
		
		// Looking for attributes to add/update
		requestBeing.getAttrs().keySet().stream()
			.forEach(requestAttrCode -> {

				Integer requestAttrValue = requestBeing.getAttrs().get(requestAttrCode);
				
				MudBeingAttr attr = 
				dbBeing.getAttrs().stream()
					.filter(d -> d.getId().getAttrCode().equals(requestAttrCode))
					.findFirst()
					.orElse(MudBeingAttrConverter.build(dbBeing.getBeingCode(), requestAttrCode, requestAttrValue));
				
				
				// Update the attribute value
				attr.setAttrValue(requestAttrValue);
				
				dbBeing.getAttrs().add(attr);
				
			});
		
		return dbBeing;
	}

}
