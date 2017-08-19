package com.jpinfo.mudengine.player;

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

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class PlayerTests {
	
	private static final String testUsername = "james";
	private static final String testPassword = "pass";
	private static final String testEmail = "email@test.com";
	private static final Long testPlayerId = 1L;
	
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testSession() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// Create Session
		urlVariables.put("username", PlayerTests.testUsername);
		urlVariables.put("password", PlayerTests.testPassword);
		
		ResponseEntity<Session> createResponse = restTemplate.exchange(
				"/player/{username}/session?password={password}", HttpMethod.PUT, null, Session.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody().getSessionId()).isNotNull();
		assertThat(createResponse.getBody().getSessionStart()).isNotNull();
		assertThat(createResponse.getBody().getPlayerId()).isNotNull();
		
		// Get Session
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.testUsername);
		
		ResponseEntity<Session> getResponse = restTemplate.exchange(
				"/player/{username}/session", HttpMethod.GET, null, Session.class, urlVariables);
		
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().getPlayerId()).isEqualTo(PlayerTests.testPlayerId);
		assertThat(getResponse.getBody().getSessionId()).isEqualTo(createResponse.getBody().getSessionId());
		
		// Delete Session
		ResponseEntity<String> deleteResponse = restTemplate.exchange(
				"/player/{username}/session", HttpMethod.DELETE, null, String.class, urlVariables);
		
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// Get Session
		getResponse = restTemplate.exchange(
				"/player/{username}/session", HttpMethod.GET, null, Session.class, urlVariables);
		
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		
	}
	
	public void testPlayer() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// *********** CREATE PLAYER ******************
		// ============================================
		
		urlVariables.put("username", PlayerTests.testUsername);
		urlVariables.put("email", PlayerTests.testEmail);
		
		ResponseEntity<Player> createResponse = restTemplate.exchange(
				"/player/{username}?email={email}", HttpMethod.PUT, null, Player.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody().getPlayerId()).isNotNull();
		assertThat(createResponse.getBody().getEmail()).isEqualTo(PlayerTests.testEmail);


		// *********** UPDATE PLAYER ******************
		// ============================================
		
		
	}
}
