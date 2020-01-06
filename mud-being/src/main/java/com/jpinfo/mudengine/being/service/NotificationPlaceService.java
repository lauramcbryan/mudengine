package com.jpinfo.mudengine.being.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.utils.NotificationMessage;

@Service
public class NotificationPlaceService {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationPlaceService.class);
	
	@Autowired
	private BeingRepository repository;
	
	@Autowired
	private MessageServiceClient messageService;
	
	public void handlePlaceNotification(NotificationMessage msg) {

		// Gathering all beings in the place
		List<MudBeing> beingListInPlace =
				repository.findPlayableInThisPlace(
						msg.getWorldName(), 
						msg.getEntityId().intValue());
		
		
		switch(msg.getEvent()) {
			case PLACE_ATTR_CHANGE:
				// FUTURE: Apply the changes in all beings in the same place
				break;
			case PLACE_EXIT_CREATE:
			case PLACE_EXIT_DESTROY:
			case PLACE_EXIT_OPEN:
			case PLACE_EXIT_CLOSE:
			case PLACE_EXIT_UNLOCK:
			case PLACE_EXIT_LOCK:
			case PLACE_CLASS_CHANGE:

				// Send a message to all beings in the same place
				beingListInPlace.stream()
					.forEach(curBeing -> {
						
						MessageRequest request = new MessageRequest();
						
						request.setMessageKey(msg.getMessageKey());
						request.setArgs(msg.getArgs());
						
						// Adding the place in the changed list
						request.addChangedEntity(EnumEntityType.PLACE, msg.getEntityId()); 

						messageService.putMessage(curBeing.getCode(), request);
						
						log.info("world: {}, entityId: {}, message: {} ",
								msg.getWorldName(), curBeing.getCode(), msg.getMessageKey());
						
					});
				
				break;
			case PLACE_DESTROY:

				// Destroy all the beings in the same place
				beingListInPlace.stream()
					.forEach(curBeing -> {
						
						// (That will trigger a BEING_DESTROY notification)
						repository.delete(curBeing);
						
						log.info("world: {}, entityId: {}, DESTROYED", msg.getWorldName(), curBeing.getCode());
					});
				
				break;
				
			default:
				break;
		
		}
		
	}
}
