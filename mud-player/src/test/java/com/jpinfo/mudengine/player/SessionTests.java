package com.jpinfo.mudengine.player;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.player.client.BeingServiceClient;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.repository.PlayerRepository;
import com.jpinfo.mudengine.player.repository.SessionRepository;
import com.jpinfo.mudengine.player.service.PlayerServiceImpl;
import com.jpinfo.mudengine.player.service.SessionServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8"})
public class SessionTests {

	
	private static final String TEST_USERNAME = "testuser";
	private static final String TEST_PASSWORD = "pass";
	private static final String TEST_CLIENT_TYPE = "text/plain";
	private static final String TEST_IP_ADDRESS = "127.0.0.1";
	
	@Autowired
	private PlayerRepository repository;
	
	@MockBean
	private BeingServiceClient beingClient;
	
	@MockBean
	private SessionRepository sessionRepository;
	
	@Autowired
	private SessionServiceImpl service;
	
	private PlayerServiceImpl playerService;
	
	@Test
	public void contextLoads() {
		
	}
	/*
	@Test
	public void testCreateSession() {
		
		Player player = playerService.getPlayerDetails();

		Session sessionData =
				service.createSession(
						player,
						SessionTests.TEST_CLIENT_TYPE, 
						SessionTests.TEST_IP_ADDRESS);
		
		assertThat(sessionData.getSessionId()).isNotNull();
		assertThat(sessionData.getSessionStart()).isNotNull();
		assertThat(sessionData.getPlayerId()).isNotNull();
		assertThat(sessionData.getClientType()).isEqualTo(SessionTests.TEST_CLIENT_TYPE);
		assertThat(sessionData.getIpAddress()).isEqualTo(SessionTests.TEST_IP_ADDRESS);
		
	}
	
	@Test
	public void testCreateSessionPendingAccount() throws Exception {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// *********** CREATE SESSION FOR A PENDING ACCOUNT ******************
		// ============== (it should return error) ===========================
		
		// First I need to get the password generated in database
		MudPlayer dbPlayer = repository.findById(PlayerTests.TEST_PENDING_PLAYER_ID)
				.orElseThrow(() -> new Exception("Test player not found in database"));
		
		// Create Session
		urlVariables.put("username", PlayerTests.TEST_PENDING_USERNAME);
		urlVariables.put("password", dbPlayer.getPassword());
		urlVariables.put("clientType", PlayerTests.TEST_CLIENT_TYPE);
		urlVariables.put("ipAddress", PlayerTests.TEST_IP_ADDRESS);

		
		ResponseEntity<Session> loginResponse = restTemplate.exchange(
				"/player/{username}/session?password={password}&clientType={clientType}&ipAddress={ipAddress}", 
				HttpMethod.PUT, null, Session.class, urlVariables);

		// "You must change your password" message
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	public void testGetSessionAsAnonymous() {
		
		// Get Session (anonymous)
		service.getActiveSession(username);
		
		ResponseEntity<Session> anonymousResponse = restTemplate.exchange(
				"/player/{username}/session", HttpMethod.GET, null, Session.class, urlVariables);
		
		assertThat(anonymousResponse.getStatusCode()).isNotEqualTo(HttpStatus.OK);
		
	}
		
	
	public void testGetSessionAsAuthenticated() {
		
		// Get Session (authenticated)
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, createResponse.getHeaders().getFirst(CommonConstants.AUTH_TOKEN_HEADER));
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestHeaders);
		
		ResponseEntity<Session> getResponse = restTemplate.exchange(
				"/player/{username}/session", HttpMethod.GET, requestEntity, Session.class, urlVariables);
		
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().getPlayerId()).isEqualTo(PlayerTests.TEST_PLAYER_ID);
		assertThat(getResponse.getBody().getSessionId()).isEqualTo(createResponse.getBody().getSessionId());
		
	}
	

	public void testSetActiveBeing() {
		
		// ********* SELECT THE BEING **************
		// =========================================
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.TEST_USERNAME_3);
		urlVariables.put("beingCode", PlayerTests.TEST_BEING_CODE);

		
		ResponseEntity<Session> selectResponse = restTemplate.exchange(
				"/{username}/session/being/{beingCode}", 
				HttpMethod.PUT, authEntity, Session.class, urlVariables);
		

		// Check if the return is successful
		assertThat(selectResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

		assertThat(selectResponse.getHeaders().get(CommonConstants.AUTH_TOKEN_HEADER)).isNotNull();

		String updatedToken = selectResponse.getHeaders().getFirst(CommonConstants.AUTH_TOKEN_HEADER);

		MudUserDetails uDetails = (MudUserDetails)
				tokenService.getAuthenticationFromToken(updatedToken).getDetails();
		
		// Check if the beingCode is set in header token
		assertThat(uDetails.getSessionData().get().getBeingCode()).isEqualTo(PlayerTests.TEST_BEING_CODE);
		
		// Check if the beingCode is set in session object
		assertThat(selectResponse.getBody().getBeingCode()).isEqualTo(PlayerTests.TEST_BEING_CODE);

	}		

	public void testDestroyActiveBeing() {
		
		// ********* DESTROY THE ACTIVE BEING **************
		// =================================================
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.TEST_USERNAME_3);
		urlVariables.put("beingCode", PlayerTests.TEST_BEING_CODE);

		
		ResponseEntity<Session> destroyResponse = restTemplate.exchange(
				"/{username}/session/being/{beingCode}", 
				HttpMethod.DELETE, authEntity, Session.class, urlVariables);
		

		// Check if the return is successful
		assertThat(destroyResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

		assertThat(destroyResponse.getHeaders().get(CommonConstants.AUTH_TOKEN_HEADER)).isNotNull();

		updatedToken = destroyResponse.getHeaders().getFirst(CommonConstants.AUTH_TOKEN_HEADER);
		
		MudUserDetails uDetails2 = (MudUserDetails)
				tokenService.getAuthenticationFromToken(updatedToken).getDetails();
		
		
		// Check if the beingCode isn't set in header token
		assertThat(uDetails2.getSessionData().get().getBeingCode()).isNull();
		
		// Check if the beingCode is set in session object
		assertThat(destroyResponse.getBody().getBeingCode()).isNull();
		
		// Check if the being is no longer in beingList
		assertThat(uDetails2.getPlayerData().get().getBeingList().stream()
			.noneMatch(d -> d.getBeingCode().equals(PlayerTests.TEST_BEING_CODE))
		).isTrue();
		
	}
	
	*/
	
	
}
