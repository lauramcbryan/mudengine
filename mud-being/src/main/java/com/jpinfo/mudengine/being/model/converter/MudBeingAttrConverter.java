package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrPK;

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
}
