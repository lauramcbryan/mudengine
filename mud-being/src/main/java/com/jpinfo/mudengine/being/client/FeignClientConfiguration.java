package com.jpinfo.mudengine.being.client;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Request.Options;

@Configuration
public class FeignClientConfiguration {
	
	@Value("${feign.client.connectionTimeout}")
	private int connectionTimeout;
	
	@Value("${feign.client.readTimeout}")
	private int readTimeout;

	@Bean
	public Options options() {
		return new Options(connectionTimeout, readTimeout);
		
	}
	
	@Bean
	public FeignClientErrorDecoder errorDecoder() {
		return new FeignClientErrorDecoder();
	}
}
