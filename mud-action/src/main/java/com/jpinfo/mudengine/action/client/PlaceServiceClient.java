package com.jpinfo.mudengine.action.client;

import com.jpinfo.mudengine.common.place.Place;

public interface PlaceServiceClient {

	Place getPlace(Integer placeId);
	
	Place updatePlace(Integer placeId, Place requestPlace);
}
