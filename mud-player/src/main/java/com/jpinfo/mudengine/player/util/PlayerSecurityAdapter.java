package com.jpinfo.mudengine.player.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.jpinfo.mudengine.common.security.CommonSecurityAdapter;

@Configuration
@EnableWebSecurity
public class PlayerSecurityAdapter extends CommonSecurityAdapter {

}
