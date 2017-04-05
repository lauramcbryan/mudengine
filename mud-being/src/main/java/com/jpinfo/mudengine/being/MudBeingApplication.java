package com.jpinfo.mudengine.being;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.jpinfo.mudengine.being.repository")
@EntityScan("com.jpinfo.mudengine.being.model")
public class MudBeingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudBeingApplication.class, args);
	}
}
