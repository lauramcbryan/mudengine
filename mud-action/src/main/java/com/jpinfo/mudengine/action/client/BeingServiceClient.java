package com.jpinfo.mudengine.action.client;

import com.jpinfo.mudengine.common.being.Being;

public interface BeingServiceClient {
	
	Being getBeing(Long beingCode);
	
	Being updateBeing(Long beingCode, Being requestBeing);

}
