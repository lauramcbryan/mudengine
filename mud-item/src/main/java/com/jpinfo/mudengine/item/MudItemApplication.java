package com.jpinfo.mudengine.item;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class MudItemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudItemApplication.class, args);
	}
	
	@Bean
	public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
		  EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
		  
		  return b;
	}		
}
