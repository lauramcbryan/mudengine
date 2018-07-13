package com.jpinfo.mudengine.common.security;

import java.io.IOException;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.jpinfo.mudengine.common.utils.CommonConstants;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@Component
public class CommonSecurityFilter extends GenericFilterBean {

	@Autowired
	private TokenService tokenService;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		try {
		
			Authentication authenticatedUser = tokenService.getAuthenticationFromToken(
					httpRequest.getHeader(CommonConstants.AUTH_TOKEN_HEADER));
			
			if (authenticatedUser!=null) {
				SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
			}
			
			filterChain.doFilter(request, response);
			
		} catch(ExpiredJwtException e) {
			
			httpResponse.setHeader(CommonConstants.ERROR_MESSAGE_HEADER, "Token expired");
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
			
		} catch(JwtException e) {
			
			httpResponse.setHeader(CommonConstants.ERROR_MESSAGE_HEADER, e.getMessage());
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
	}

}
