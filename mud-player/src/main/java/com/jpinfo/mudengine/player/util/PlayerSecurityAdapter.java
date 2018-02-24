package com.jpinfo.mudengine.player.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jpinfo.mudengine.common.security.CommonSecurityFilter;

@Configuration
@EnableWebSecurity
public class PlayerSecurityAdapter extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		
		// The order of the matchers here is important:
		// The most specific rules should go first
		// otherwise your request may be evaluated by a wider rule
		// that is in higher position and allowed/disallowed by mistake
		http.
			authorizeRequests()
			.antMatchers(HttpMethod.PUT, "/player/{\\x+}").permitAll()				// allows anonymous access to createPlayer
			.antMatchers(HttpMethod.POST, "/player/{\\x+}/password").permitAll()	// allows anonymous access to changePassword
			.antMatchers(HttpMethod.PUT, "/player/{\\x+}/session").permitAll()		// allows anonymous access to login service
			.antMatchers("/player/*").authenticated()								// protected everything else under /player
		;

		http.addFilterBefore(new CommonSecurityFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
