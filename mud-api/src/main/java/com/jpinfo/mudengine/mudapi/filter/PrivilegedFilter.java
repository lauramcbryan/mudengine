package com.jpinfo.mudengine.mudapi.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class PrivilegedFilter extends ZuulFilter {

	@Override
	public Object run() throws ZuulException {
		
		RequestContext ctx = RequestContext.getCurrentContext();
		
		HttpServletRequest request = ctx.getRequest();
		
		String uri = request.getRequestURI();
		
		if ((uri.startsWith("/mud-world")) ||
			(uri.startsWith("/mud-item")) ||
			(uri.startsWith("/mud-being"))) {
			
			if (!request.getMethod().equals("GET")) {
				ctx.setSendZuulResponse(false);
				try {
					ctx.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN, "Operation not allowed");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	@Override
	public boolean shouldFilter() {
		return true;
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
