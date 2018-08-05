package com.jpinfo.mudengine.world.model.converter;

import com.jpinfo.mudengine.world.model.MudPlaceAttr;
import com.jpinfo.mudengine.world.model.MudPlaceClassAttr;
import com.jpinfo.mudengine.world.model.pk.PlaceAttrPK;

public class MudPlaceAttrConverter {

	private MudPlaceAttrConverter() { }
	
	public static MudPlaceAttr convert(Integer placeCode, MudPlaceClassAttr classAttr) {
		
		MudPlaceAttr response = new MudPlaceAttr();
		PlaceAttrPK pk = new PlaceAttrPK();
		
		pk.setAttrCode(classAttr.getId().getAttrCode());
		pk.setPlaceCode(placeCode);
		
		response.setId(pk);
		response.setAttrValue(classAttr.getAttrValue());
		
		return response;
	}
	
	public static MudPlaceAttr build(Integer placeCode, String attrCode, Integer attrValue) {
		
		MudPlaceAttr response = new MudPlaceAttr();
		PlaceAttrPK pk = new PlaceAttrPK();
		
		pk.setAttrCode(attrCode);
		pk.setPlaceCode(placeCode);
		
		response.setId(pk);
		response.setAttrValue(attrValue);
		
		return response;
	}

}
