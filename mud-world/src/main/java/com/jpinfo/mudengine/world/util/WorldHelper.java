package com.jpinfo.mudengine.world.util;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.placeClass.PlaceClass;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceClass;

public class WorldHelper {

	public static Place buildPlace(MudPlace a) {
		
		return new Place();
	}
	
	public static PlaceClass buildPlaceClass(MudPlaceClass a) {
		
		return new PlaceClass();
	}
}
