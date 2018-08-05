package com.jpinfo.mudengine.item.model.converter;

import com.jpinfo.mudengine.item.model.MudItemAttr;
import com.jpinfo.mudengine.item.model.MudItemClassAttr;
import com.jpinfo.mudengine.item.model.pk.MudItemAttrPK;

public class MudItemAttrConverter {

	private MudItemAttrConverter() { }
	
	public static MudItemAttr build(Long itemCode, MudItemClassAttr classAttr) {
		return build(itemCode, classAttr.getAttrCode(), classAttr.getAttrValue());
	}
	
	public static MudItemAttr build(Long itemCode, String attrCode, Integer attrValue) {
		
		MudItemAttr response = new MudItemAttr();
		MudItemAttrPK pk = new MudItemAttrPK();
		
		pk.setAttrCode(attrCode);
		pk.setItemCode(itemCode);
		
		response.setId(pk);
		response.setAttrValue(attrValue);
		
		return response;
	}
}
