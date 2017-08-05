package com.jpinfo.mudengine.being;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.being.Being;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class BeingTests {
	
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testCRUD() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// *********** CREATE **********
		// =============================
		

		ResponseEntity<Being> responseCreate= restTemplate.exchange("/being", HttpMethod.PUT, null, Being.class, urlVariables);
		
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		
		// ************ READ ***********
		// =============================
		
		ResponseEntity<Being> responseRead= restTemplate.exchange("/being", HttpMethod.GET, null, Being.class, urlVariables);
		
		
		// *********** UPDATE **********
		// =============================
		
		
		
		// *********** DELETE **********
		// =============================

	}

}
