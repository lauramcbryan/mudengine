package com.jpinfo.mudengine.action.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.jpinfo.mudengine.common.security.CommonNoSecurityAdapter;

@Profile("itgtest")
@Configuration
@EnableWebSecurity
public class NoSecurityAdapter extends CommonNoSecurityAdapter {

}
