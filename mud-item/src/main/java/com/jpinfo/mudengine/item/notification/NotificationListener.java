package com.jpinfo.mudengine.item.notification;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
	
	@Autowired
	private NotificationService service;
	
	@Autowired
	private TokenService tokenService;

	@JmsListener(destination="${place.topic:disabled}")
	@JmsListener(destination="${being.topic:disabled}")
	public void receiveNotification(
			@Header(name=CommonConstants.AUTH_TOKEN_HEADER) String authToken, 
			@Payload NotificationMessage msg) {
		
		try {
			// Saving the security context in this thread
			// (Will be used further below)
			SecurityContextHolder.getContext().setAuthentication(
					tokenService.getAuthenticationFromToken(authToken)
					);
		} catch(IOException e) {
			// Exception caught while trying to process the authentication token provided
			// Just log it and discard.
			log.warn("Corrupted auth token received in notification.  Discarding the message.");
			
			return;
		}

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
