package com.jpinfo.mudengine.common.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class CommonSecurityFilter extends GenericFilterBean {


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		
		Authentication authenticatedUser = TokenService.getAuthenticationFromToken(
				httpRequest.getHeader(TokenService.HEADER_TOKEN));
		
		if (authenticatedUser!=null) {
			SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
		}
		
		filterChain.doFilter(request, response);
	}

}
