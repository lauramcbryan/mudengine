package com.jpinfo.mudengine.player.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jpinfo.mudengine.common.security.CommonSecurityFilter;

@Configuration
@EnableWebSecurity
public class PlayerSecurityAdapter extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/player/{\\x+}/session").permitAll();
		http.authorizeRequests().anyRequest().authenticated();
		http.addFilterBefore(new CommonSecurityFilter(), UsernamePasswordAuthenticationFilter.class);

		//super.configure(http);
	}

}
