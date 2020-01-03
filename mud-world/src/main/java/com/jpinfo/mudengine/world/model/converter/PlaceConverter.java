package com.jpinfo.mudengine.world.model.converter;

import java.util.stream.Collectors;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceAttr;
import com.jpinfo.mudengine.world.model.MudPlaceExit;

public class PlaceConverter {
	
	private PlaceConverter() {
		
	}
	
	public static Place convert(MudPlace originalDbPlace) {
		
		Place result = new Place();
		
		if (originalDbPlace!=null) {
		
			result.setCode(originalDbPlace.getCode());
			
			result.setClassCode(originalDbPlace.getPlaceClass().getCode());
			
			result.setName(originalDbPlace.getName()!=null ? 
						originalDbPlace.getName() : 
						originalDbPlace.getPlaceClass().getName());
			
			result.setDescription(originalDbPlace.getDescription()!=null ?
						originalDbPlace.getDescription() :
						originalDbPlace.getPlaceClass().getDescription());
					
			result.setPlaceClass(
					PlaceClassConverter.convert(originalDbPlace.getPlaceClass())
							);

			// Map the database list with the exits in a map
			result.setExits(
				originalDbPlace.getExits().stream()
					.collect(Collectors.toMap(
							MudPlaceExit::getDirection,
							PlaceExitConverter::convert
							))
					);
	
			// Map the database attributes
			result.setAttrs(
				originalDbPlace.getAttrs().stream()
					.collect(Collectors.toMap(
							MudPlaceAttr::getCode, 
							MudPlaceAttr::getValue))
					);
		}
		
		
		return result;
	}
}
