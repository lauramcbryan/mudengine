package com.jpinfo.mudengine.common.utils;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;

import org.springframework.security.core.Authentication;

public class LogFilter extends GenericFilterBean {
	
	private static final Logger log = LoggerFactory.getLogger(LogFilter.class);
	
	private static final String ANONYMOUS_LOG_LINE = "world: {none}, session: {none}, being: {none}, player: {none}, ";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		if (log.isInfoEnabled()) {
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			HttpServletResponse httpResponse = (HttpServletResponse)response;
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			
			StringBuilder logLine = null;
			
			if (auth!=null) {
				
				MudUserDetails uDetails = (MudUserDetails)auth.getDetails();
				
				if (uDetails.getSessionData()!=null) {
					
					logLine = buildLoggedLine(uDetails.getSessionData());
				}
				else {
					logLine = buildAnonymousLine();
				}
			} else {
				logLine = buildAnonymousLine();
			}
			
			logLine.append("operation: {").append(httpRequest.getMethod()).append("}, ");
			logLine.append("uri: {").append(httpRequest.getRequestURI()).append("}, ");
			logLine.append("httpStatus: {").append(httpResponse.getStatus()).append("}, ");
			
			log.info(logLine.toString());
		}
		
		chain.doFilter(request, response);
	}
	
	private StringBuilder buildLoggedLine(Session session) {
		
		StringBuilder logMessage = new StringBuilder();
		
		logMessage.append("world: {").append(session.getCurWorldName()!=null ? session.getCurWorldName(): "none").append("}, ");
		logMessage.append("session: {").append(session.getSessionId()!=null ? session.getSessionId(): "none").append("}, ");
		logMessage.append("being: {").append(session.getBeingCode()!=null ? session.getBeingCode(): "none").append("}, ");
		logMessage.append("player: {").append(session.getPlayerId()!=null ? session.getPlayerId(): "none").append("}, ");
		
		return logMessage;
	}
	
	private StringBuilder buildAnonymousLine() {
		
		return new StringBuilder(ANONYMOUS_LOG_LINE);
	}

}
