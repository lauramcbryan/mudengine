package com.jpinfo.mudengine.config.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("/refreshcfg")
public class RefreshController {
	
	private static final DateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	@RequestMapping(method=RequestMethod.GET, value="/")
	public String refreshConfig() {
				
		return "Last refresh: "+ dt.format(new Date());
	}

}
