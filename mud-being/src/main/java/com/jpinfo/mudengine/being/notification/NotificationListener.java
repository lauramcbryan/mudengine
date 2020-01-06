package com.jpinfo.mudengine.being.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.being.service.NotificationItemService;
import com.jpinfo.mudengine.being.service.NotificationPlaceService;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.NotificationMessage;

@Component
@Profile({"ti", "qa", "prod"})
public class NotificationListener {

	@Autowired
	private NotificationItemService itemService;
	
	@Autowired
	private NotificationPlaceService placeService;
	
	@Autowired
	private TokenService tokenService;
	
	
	@JmsListener(destination="${place.topic}")
	@JmsListener(destination="${item.topic}")
	public void receiveNotification(
			@Header(name=CommonConstants.AUTH_TOKEN_HEADER) String authToken, 
			@Payload NotificationMessage msg) {
		
		SecurityContextHolder.getContext().setAuthentication(
				tokenService.getAuthenticationFromToken(authToken)
				);

		switch(msg.getEntity()) {
		case ITEM:
			itemService.handleItemNotification(msg);
			break;
		case PLACE:
			placeService.handlePlaceNotification(msg);
			break;
		case BEING:
			// This listener isn't supposed to handle being notifications,
			// since being effects was already made in the original operation 
			// (or in NotificationAspect, where it was detected)
			// Therefore, being events are just ignored here.
		default:
			break;
		
		}
		
	}
	
}
