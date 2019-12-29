package com.jpinfo.mudengine.item.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.item.service.NotificationService;

@Component
@Profile({"ti", "qa", "prod"})
public class NotificationListener {
	
	@Autowired
	private NotificationService service;
	
	@Autowired
	private TokenService tokenService;

	@JmsListener(destination="${place.topic:disabled}")
	@JmsListener(destination="${being.topic:disabled}")
	public void receiveNotification(
			@Header(name=CommonConstants.AUTH_TOKEN_HEADER) String authToken, 
			@Payload NotificationMessage msg) {
		
		// Saving the security context in this thread
		// (Will be used further below)
		SecurityContextHolder.getContext().setAuthentication(
				tokenService.getAuthenticationFromToken(authToken)
				);

		switch(msg.getEntity()) {
		case ITEM:
			break;
		case PLACE:
			service.handlePlaceNotification(msg);
			break;
		case BEING:
			service.handleBeingNotification(msg);
			break;
		default:
			break;
		
		}
	}
}
