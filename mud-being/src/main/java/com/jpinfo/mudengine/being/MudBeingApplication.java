package com.jpinfo.mudengine.being;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication(scanBasePackages= {
		"com.jpinfo.mudengine.being",
		"com.jpinfo.mudengine.common"
})
@EnableJms
public class MudBeingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudBeingApplication.class, args);
	}	
}
