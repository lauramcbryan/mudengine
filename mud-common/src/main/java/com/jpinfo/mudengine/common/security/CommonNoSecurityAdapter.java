package com.jpinfo.mudengine.common.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class CommonNoSecurityAdapter extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// That's needed for web h2 console to work
		http.csrf().disable();
		http.headers().frameOptions().disable();
		
		http.authorizeRequests().anyRequest().permitAll();
	}

}
