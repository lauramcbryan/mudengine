package com.jpinfo.mudengine.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jpinfo.mudengine.common.utils.LogFilter;

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
		
		// The order of the matchers here is important:
		// The most specific rules should go first
		// otherwise your request may be evaluated by a wider rule
		// that is in higher position and allowed/disallowed by mistake
		http.
			authorizeRequests()
			.antMatchers(HttpMethod.PUT, "/player/{\\x+}").permitAll()				// allows anonymous access to createPlayer
			.antMatchers(HttpMethod.POST, "/player/{\\x+}/password").permitAll()	// allows anonymous access to changePassword
			.antMatchers(HttpMethod.PUT, "/player/{\\x+}/session").permitAll()		// allows anonymous access to login service
			.antMatchers("/player/**").authenticated()								// protected everything else under /player
			.antMatchers("/action/class/**").permitAll()
			.antMatchers("/action/**").authenticated()
			.antMatchers("/item/**").authenticated()
			.antMatchers("/being/**").authenticated()
			.antMatchers("/place/**").authenticated()
			.antMatchers("/message/**").authenticated();
		
		http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
		
		http.addFilterAfter(new LogFilter(), CommonSecurityFilter.class);
	}

}
