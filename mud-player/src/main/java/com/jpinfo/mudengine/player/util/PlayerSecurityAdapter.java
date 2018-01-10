package com.jpinfo.mudengine.player.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jpinfo.mudengine.common.security.CommonSecurityFilter;

@Profile("!default")
@Configuration
@EnableWebSecurity
public class PlayerSecurityAdapter extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		http.authorizeRequests().antMatchers("/player/*").authenticated();
		//http.authorizeRequests().antMatchers(HttpMethod.PUT, "/player/{\\x+}/session").permitAll();			
		//http.authorizeRequests().antMatchers(HttpMethod.POST, "/player/{\\x+}/password").permitAll();
		//http.authorizeRequests().antMatchers(HttpMethod.GET, "/health").permitAll();
		//http.authorizeRequests().antMatchers(HttpMethod.PUT, "/player/{\\x+}").permitAll();		
		http.addFilterBefore(new CommonSecurityFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
