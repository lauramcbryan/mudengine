package com.jpinfo.mudengine.common.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CommonSecurityAdapter extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/player/{\\x+}/session").permitAll();			
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/player/{\\x+}/password").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/player/{\\x+}").permitAll();
		http.authorizeRequests().anyRequest().authenticated();
		http.addFilterBefore(new CommonSecurityFilter(), UsernamePasswordAuthenticationFilter.class);
	}

}
