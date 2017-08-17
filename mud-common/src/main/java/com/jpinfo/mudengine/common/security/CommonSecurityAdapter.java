package com.jpinfo.mudengine.common.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CommonSecurityAdapter extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().anyRequest().authenticated();
		http.authorizeRequests().antMatchers("/player/session").permitAll();
		http.addFilterBefore(new CommonSecurityFilter(), UsernamePasswordAuthenticationFilter.class);
	}

}
