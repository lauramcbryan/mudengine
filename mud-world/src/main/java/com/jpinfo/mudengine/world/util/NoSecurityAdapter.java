package com.jpinfo.mudengine.world.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Profile("default")
@Configuration
@EnableWebSecurity
public class NoSecurityAdapter extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// That's needed for web h2 console to work
		http.csrf().disable();
		http.headers().frameOptions().disable();
		
		http.authorizeRequests().anyRequest().permitAll();
	}

}
