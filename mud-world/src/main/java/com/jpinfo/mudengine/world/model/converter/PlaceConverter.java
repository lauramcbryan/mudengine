package com.jpinfo.mudengine.world.model.converter;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceAttr;
import com.jpinfo.mudengine.world.model.MudPlaceExit;

@Component
public class PlaceConverter {
	
	
	@Autowired
	private PlaceExitConverter exitConverter;
	
	public Place convert(MudPlace originalDbPlace) {
		
		Place result = new Place();
		
		if (originalDbPlace!=null) {
		
			result.setPlaceCode(originalDbPlace.getPlaceCode());
			
			result.setPlaceClassCode(originalDbPlace.getPlaceClass().getPlaceClassCode());
			
			result.setPlaceName(originalDbPlace.getPlaceName()!=null ? 
						originalDbPlace.getPlaceName() : 
						originalDbPlace.getPlaceClass().getName());
					
			result.setPlaceClass(
					PlaceClassConverter.convert(originalDbPlace.getPlaceClass())
							);

			// Map the database list with the exits in a map
			result.setExits(
				originalDbPlace.getExits().stream()
					.collect(Collectors.toMap(
							MudPlaceExit::getDirection, 
							exitConverter::convert))
					);
	
			// Map the database attributes
			result.setAttrs(
				originalDbPlace.getAttrs().stream()
					.collect(Collectors.toMap(
							MudPlaceAttr::getAttrCode, 
							MudPlaceAttr::getAttrValue))
					);
		}
		
		
		return result;
	}
}
