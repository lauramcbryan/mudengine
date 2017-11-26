package com.jpinfo.mudengine.world.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.jpinfo.mudengine.common.security.CommonSecurityAdapter;

@Profile("prod")
@Configuration
@EnableWebSecurity
public class WebSecurityAdapter extends CommonSecurityAdapter {

}
