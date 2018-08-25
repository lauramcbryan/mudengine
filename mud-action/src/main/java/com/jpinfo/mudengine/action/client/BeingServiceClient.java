package com.jpinfo.mudengine.action.client;

import java.util.List;

import com.jpinfo.mudengine.common.being.Being;

public interface BeingServiceClient {
	
	Being getBeing(Long beingCode);
	
	Being updateBeing(Long beingCode, Being requestBeing);
	
	List<Being> getAllFromPlace(String worldName, Integer placeCode);
}
