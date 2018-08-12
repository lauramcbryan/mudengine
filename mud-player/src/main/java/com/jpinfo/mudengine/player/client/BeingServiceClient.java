package com.jpinfo.mudengine.player.client;

import com.jpinfo.mudengine.common.being.Being;

public interface BeingServiceClient  {
	
	Being getBeing(Long beingCode);

	Being createPlayerBeing(Long playerId, String beingClass, String worldName,
			Integer placeCode, String beingName);

	void destroyBeing(Long beingCode);
}
