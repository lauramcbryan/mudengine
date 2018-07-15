package com.jpinfo.mudengine.world.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jpinfo.mudengine.common.security.CommonSecurityFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityAdapter extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private CommonSecurityFilter securityFilter;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Disabling cross-site request forgery in order to be able to use H2 Console
		http.csrf().disable();
		
		// Bypassing frame options security in order to be able to use H2 Console
		http.headers().frameOptions().disable();
		
		http.authorizeRequests().antMatchers("/place/*").authenticated();
		http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
	}

}
