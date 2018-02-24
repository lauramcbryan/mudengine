package com.jpinfo.mudengine.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.player.client.BeingServiceClient;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.model.MudPlayerBeing;
import com.jpinfo.mudengine.player.model.pk.MudPlayerBeingPK;
import com.jpinfo.mudengine.player.repository.PlayerRepository;

import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class PlayerTests {
	
	private static final Long TEST_PLAYER_ID = 1L;
	private static final Long TEST_PENDING_PLAYER_ID = 2L;

	private static final String TEST_USERNAME = "testuser";
	private static final String TEST_PASSWORD = "pass";
	private static final String TEST_LOCALE = "pt_BR";
	private static final String TEST_EMAIL = "email@test.com";

	private static final String TEST_PENDING_USERNAME = "pendinguser";
	
	private static final String TEST_USERNAME_2 = "josiel2";
	private static final String TEST_LOCALE_2 = "en_US";
	private static final String TEST_EMAIL_2 = "changed@test.com";

	private static final String TEST_CLIENT_TYPE = "text/plain";
	private static final String TEST_IP_ADDRESS = "127.0.0.1";

	private static final Long TEST_BEING_CODE = 1L;
	private static final String TEST_BEING_CLASS = "beingClass";
	private static final String TEST_BEING_CLASS_NAME = "beingClassName";
	private static final String TEST_BEING_NAME = "beingName";

	private static final String TEST_WORLD_NAME = "worldName";
	private static final Integer TEST_PLACE_CODE = 2;

	
	private static final String TEST_USERNAME_3 = "sessionuser";

	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private PlayerRepository repository;
	
	@MockBean
	private BeingServiceClient beingClient;
	

	private void setupMocks() {
		
		Being createdBeing = new Being();
		
		createdBeing.setBeingClass(new BeingClass());
		createdBeing.getBeingClass().setName(TEST_BEING_CLASS_NAME);
		createdBeing.setName(TEST_BEING_NAME);
		createdBeing.setPlayerId(PlayerTests.TEST_PLAYER_ID);
		createdBeing.setBeingCode(PlayerTests.TEST_BEING_CODE);
		
		ResponseEntity<Being> responseBeing = new ResponseEntity<Being>(createdBeing, HttpStatus.CREATED);
		
		given(this.beingClient.createBeing(anyString(), 
				eq(Being.BEING_TYPE_PLAYER),
				eq(PlayerTests.TEST_BEING_CLASS),
				eq(PlayerTests.TEST_WORLD_NAME),
				eq(PlayerTests.TEST_PLACE_CODE),
				any(), any(), eq(Optional.of(PlayerTests.TEST_BEING_NAME)))).willReturn(responseBeing);
	}
	
	/**
	 * Create the internal authentication token
	 * and put it in a HttpHeader
	 * @return
	 */
	private HttpHeaders getInternalAuthHeaders() {
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(TokenService.HEADER_TOKEN, TokenService.buildInternalToken());
		
		return authHeaders;
	}
	
	private HttpHeaders getAuthHeaders(String userName) {
		HttpHeaders authHeaders = new HttpHeaders();
		
		Player playerData = new Player();
		playerData.setUsername(TokenService.INTERNAL_ACCOUNT);
		playerData.setPlayerId(TokenService.INTERNAL_PLAYER_ID);
		
		Session sessionData = new Session();
		//sessionData.setBeingCode(beingCode);
		sessionData.setLocale(TokenService.INTERNAL_LOCALE);
		
		String usToken = TokenService.buildToken(userName, playerData, sessionData);		
		
		authHeaders.add(TokenService.HEADER_TOKEN, usToken);
		
		return authHeaders;
	}
	
	
	@Test
	public void contextLoads() {
	}

	@Test
	public void testSession() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// Create Session
		urlVariables.put("username", PlayerTests.TEST_USERNAME);
		urlVariables.put("password", PlayerTests.TEST_PASSWORD);
		urlVariables.put("clientType", PlayerTests.TEST_CLIENT_TYPE);
		urlVariables.put("ipAddress", PlayerTests.TEST_IP_ADDRESS);
		
		ResponseEntity<Session> createResponse = restTemplate.exchange(
				"/player/{username}/session?password={password}&clientType={clientType}&ipAddress={ipAddress}", 
				HttpMethod.PUT, null, Session.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		Session sessionData = createResponse.getBody();
		
		assertThat(sessionData.getSessionId()).isNotNull();
		assertThat(sessionData.getSessionStart()).isNotNull();
		assertThat(sessionData.getPlayerId()).isNotNull();
		assertThat(sessionData.getClientType()).isEqualTo(PlayerTests.TEST_CLIENT_TYPE);
		assertThat(sessionData.getIpAddress()).isEqualTo(PlayerTests.TEST_IP_ADDRESS);
		
		assertThat(createResponse.getHeaders().containsKey(TokenService.HEADER_TOKEN));
		
		
		// Get Session (anonymous)
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.TEST_USERNAME);
		
		ResponseEntity<Session> anonymousResponse = restTemplate.exchange(
				"/player/{username}/session", HttpMethod.GET, null, Session.class, urlVariables);
		
		assertThat(anonymousResponse.getStatusCode()).isNotEqualTo(HttpStatus.OK);
		
		
		// Get Session (authenticated)
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add(TokenService.HEADER_TOKEN, createResponse.getHeaders().getFirst(TokenService.HEADER_TOKEN));
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestHeaders);
		
		ResponseEntity<Session> getResponse = restTemplate.exchange(
				"/player/{username}/session", HttpMethod.GET, requestEntity, Session.class, urlVariables);
		
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().getPlayerId()).isEqualTo(PlayerTests.TEST_PLAYER_ID);
		assertThat(getResponse.getBody().getSessionId()).isEqualTo(createResponse.getBody().getSessionId());
		
	}
	
	@Test
	public void testPlayer() {
	
		HttpEntity<Object> internalAuthEntity = new HttpEntity<Object>(getInternalAuthHeaders());
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// *********** CREATE PLAYER ******************
		// ============================================
		
		urlVariables.put("username", PlayerTests.TEST_USERNAME_2);
		urlVariables.put("email", PlayerTests.TEST_EMAIL);
		urlVariables.put("locale" , PlayerTests.TEST_LOCALE);
		
		ResponseEntity<Player> createResponse = restTemplate.exchange(
				"/player/{username}?email={email}&locale={locale}", HttpMethod.PUT, null, Player.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		Player playerData = createResponse.getBody();
		
		assertThat(playerData.getUsername()).isEqualTo(PlayerTests.TEST_USERNAME_2);
		assertThat(playerData.getEmail()).isEqualTo(PlayerTests.TEST_EMAIL);
		assertThat(playerData.getLocale()).isEqualTo(PlayerTests.TEST_LOCALE);
		
		
		
		// *********** GET PLAYER ********************
		// ===========================================
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.TEST_USERNAME_2);
		
		ResponseEntity<Player> getResponse = restTemplate.exchange(
				"/player/{username}", HttpMethod.GET, internalAuthEntity, Player.class, urlVariables);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().getPlayerId()).isNotNull();
		assertThat(getResponse.getBody().getLocale()).isEqualTo(PlayerTests.TEST_LOCALE);
		assertThat(getResponse.getBody().getUsername()).isEqualTo(PlayerTests.TEST_USERNAME_2);
		assertThat(getResponse.getBody().getEmail()).isEqualTo(PlayerTests.TEST_EMAIL);
		
		// *********** UPDATE PLAYER ******************
		// ============================================
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.TEST_USERNAME);
		
		Player updatePlayer = new Player();
		
		updatePlayer.setUsername(PlayerTests.TEST_USERNAME_2);
		updatePlayer.setLocale(PlayerTests.TEST_LOCALE_2);
		updatePlayer.setEmail(PlayerTests.TEST_EMAIL_2);
		
		HttpEntity<Player> changePlayerHttpEntity = new HttpEntity<Player>(updatePlayer, getInternalAuthHeaders());
		
		ResponseEntity<Player> updateResponse = restTemplate.exchange(
				"/player/{username}", HttpMethod.POST, changePlayerHttpEntity, Player.class, urlVariables);
		
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		assertThat(updateResponse.getBody().getUsername()).isEqualTo(PlayerTests.TEST_USERNAME_2);
		assertThat(updateResponse.getBody().getLocale()).isEqualTo(PlayerTests.TEST_LOCALE_2);
		assertThat(updateResponse.getBody().getEmail()).isEqualTo(PlayerTests.TEST_EMAIL_2);
		assertThat(updateResponse.getBody().getStatus()).isEqualTo(Player.STATUS_PENDING);
	}
	
	@Test
	public void testPendingAccount() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// *********** CREATE SESSION FOR A PENDING ACCOUNT ******************
		// ============== (it should return error) ===========================
		
		// First I need to get the password generated in database
		MudPlayer dbPlayer = repository.findOne(PlayerTests.TEST_PENDING_PLAYER_ID);
		
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
		
		
		// **************** CHANGING PASSWORD  ************************
		// ============================================================
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.TEST_PENDING_USERNAME);
		urlVariables.put("activationCode", dbPlayer.getPassword());
		urlVariables.put("newPassword", PlayerTests.TEST_PASSWORD);
		
		ResponseEntity<String> changePasswordResponse = restTemplate.exchange(
				"/player/{username}/password?activationCode={activationCode}&newPassword={newPassword}", 
				HttpMethod.POST, null, String.class, urlVariables);
		
		assertThat(changePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// *********** CREATE SESSION FOR AN ACTIVATED ACCOUNT *****************
		// ==================== (it should work now) ===========================
		
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.TEST_PENDING_USERNAME);
		urlVariables.put("password", PlayerTests.TEST_PASSWORD);
		urlVariables.put("clientType", PlayerTests.TEST_CLIENT_TYPE);
		urlVariables.put("ipAddress", PlayerTests.TEST_IP_ADDRESS);

		
		loginResponse = restTemplate.exchange(
				"/player/{username}/session?password={password}&clientType={clientType}&ipAddress={ipAddress}", 
				HttpMethod.PUT, null, Session.class, urlVariables);
		
		
		assertThat(loginResponse.getHeaders().containsKey(TokenService.HEADER_TOKEN));
		
	}
	
	@Test
	public void testAnotherPlayerAccess() {

		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		
		HttpEntity<Object> authEntity = new HttpEntity<Object>(getAuthHeaders(PlayerTests.TEST_USERNAME));
	
		// *********** GET ANOTHER PLAYER (forbidden)********************
		// ==============================================================
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.TEST_USERNAME_2);
		
		ResponseEntity<Player> getAnotherResponse = restTemplate.exchange(
				"/player/{username}", HttpMethod.GET, authEntity, Player.class, urlVariables);

		assertThat(getAnotherResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
	}
	
	@Test
	public void testInternalAccountAccess() {

		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add(TokenService.HEADER_TOKEN, TokenService.buildInternalToken());
		HttpEntity<Object> authRequestEntity = new HttpEntity<Object>(requestHeaders);
		
		// *********** GET PLAYER (internal account) ********************
		// ==============================================================
		urlVariables.clear();
		urlVariables.put("username", PlayerTests.TEST_USERNAME);
		
		ResponseEntity<Player> getResponse = restTemplate.exchange(
				"/player/{username}", HttpMethod.GET, authRequestEntity, Player.class, urlVariables);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	//@Test
	public void testCreateBeing() {
		
		setupMocks();
		
		// Create the token
		HttpEntity<Object> authEntity = new HttpEntity<Object>(getAuthHeaders(PlayerTests.TEST_USERNAME_3));
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// ********* CREATE THE BEING **************
		// =========================================
		
		urlVariables.put("username", PlayerTests.TEST_USERNAME_3);
		urlVariables.put("beingClass", PlayerTests.TEST_BEING_CLASS_NAME);
		urlVariables.put("beingName", PlayerTests.TEST_BEING_NAME);
		urlVariables.put("worldName", PlayerTests.TEST_WORLD_NAME);
		urlVariables.put("placeCode", PlayerTests.TEST_PLACE_CODE);
		
		ResponseEntity<Session> createResponse = restTemplate.exchange(
				"/player/{username}/being?beingClass={beingClass}&beingName={beingName}&worldName={worldName}&placeCode={placeCode}", 
				HttpMethod.PUT, authEntity, Session.class, urlVariables);

		// Check if the return is successful
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Check if the being is persisted in database
		MudPlayer dbPlayer = repository.findOne(createResponse.getBody().getPlayerId());
		
		MudPlayerBeing dbBeing = new MudPlayerBeing();
		MudPlayerBeingPK pk = new MudPlayerBeingPK();
		dbBeing.setId(pk);
		
		pk.setPlayerId(createResponse.getBody().getPlayerId());
		pk.setBeingCode(PlayerTests.TEST_BEING_CODE);
		
		assertThat(dbPlayer.getBeingList()).contains(dbBeing);
		
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

		assertThat(selectResponse.getHeaders().get(TokenService.HEADER_TOKEN)).isNotNull();

		String updatedToken = selectResponse.getHeaders().getFirst(TokenService.HEADER_TOKEN);
		
		// Check if the beingCode is set in header token
		assertThat(TokenService.getBeingCodeFromToken(updatedToken)).isEqualTo(PlayerTests.TEST_BEING_CODE);
		
		// Check if the beingCode is set in session object
		assertThat(selectResponse.getBody().getBeingCode()).isEqualTo(PlayerTests.TEST_BEING_CODE);

		
		
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

		assertThat(destroyResponse.getHeaders().get(TokenService.HEADER_TOKEN)).isNotNull();

		updatedToken = destroyResponse.getHeaders().getFirst(TokenService.HEADER_TOKEN);
		
		// Check if the beingCode isn't set in header token
		assertThat(TokenService.getBeingCodeFromToken(updatedToken)).isNull();
		
		// Check if the beingCode is set in session object
		assertThat(destroyResponse.getBody().getBeingCode()).isNull();
		
	}
}
