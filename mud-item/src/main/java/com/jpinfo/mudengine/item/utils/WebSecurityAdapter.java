package com.jpinfo.mudengine.item.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.jpinfo.mudengine.common.security.CommonSecurityAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityAdapter extends CommonSecurityAdapter {

}
