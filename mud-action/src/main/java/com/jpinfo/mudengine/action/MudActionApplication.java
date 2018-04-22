package com.jpinfo.mudengine.action;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class MudActionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudActionApplication.class, args);
	}
	
	@Bean
	public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
		  EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
		  
		  return b;
	}		
}
