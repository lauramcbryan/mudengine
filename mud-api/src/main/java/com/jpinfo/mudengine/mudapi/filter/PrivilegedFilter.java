package com.jpinfo.mudengine.mudapi.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class PrivilegedFilter extends ZuulFilter {
	
	private static final Logger log = LoggerFactory.getLogger(PrivilegedFilter.class);

	@Override
	public Object run() throws ZuulException {
		
		RequestContext ctx = RequestContext.getCurrentContext();
		
		HttpServletRequest request = ctx.getRequest();
		
		String uri = request.getRequestURI();
		
		if (((uri.startsWith("/place")) ||
			(uri.startsWith("/item")) ||
			(uri.startsWith("/being"))) &&
			(!request.getMethod().equals("GET"))) {
				
			ctx.setSendZuulResponse(false);
			try {
				ctx.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN, "Operation not allowed");
			} catch (IOException e) {
				log.error("Error trying to forward an error message", e);
				
			}
		}
		
		return null;
	}

	@Override
	public boolean shouldFilter() {
		
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		
		String uri = request.getRequestURI();
		String method = request.getMethod();

		return (((uri.startsWith("/place")) || (uri.startsWith("/item")) || (uri.startsWith("/being"))) && 
				(!method.equals("GET")));
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	@Override
	public String filterType() {
		return "pre";
	}

}
