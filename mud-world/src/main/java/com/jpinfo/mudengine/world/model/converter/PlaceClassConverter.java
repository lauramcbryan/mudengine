package com.jpinfo.mudengine.world.model.converter;

import java.util.stream.Collectors;

import com.jpinfo.mudengine.common.placeclass.PlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceClassAttr;

public class PlaceClassConverter {

	private PlaceClassConverter() {}
	
	public static PlaceClass convert(MudPlaceClass a) {
		
		PlaceClass response = new PlaceClass();
		
		response.setPlaceClassCode(a.getCode());
		response.setName(a.getName());
		response.setDescription(a.getDescription());
		response.setSizeCapacity(a.getSizeCapacity());
		response.setWeightCapacity(a.getWeightCapacity());
		response.setParentClassCode(a.getParentClassCode());
		response.setDemisePlaceClassCode(a.getDemisedPlaceClassCode());
		response.setBuildEffort(a.getBuildEffort());
		response.setBuildCost(a.getBuildCost());
		
		response.setAttrs(
			a.getAttrs().stream()
				.collect(Collectors.toMap(
						MudPlaceClassAttr::getCode, 
						MudPlaceClassAttr::getValue)
						)
				);
		
		return response;
	}
}
