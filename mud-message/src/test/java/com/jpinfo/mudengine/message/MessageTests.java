package com.jpinfo.mudengine.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.message.client.BeingServiceClient;

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class MessageTests {
	
	public static final String TEST_USERNAME = "username";
	public static final Long TEST_PLAYER_ID = 1L;
	
	public static final String TEST_LOCALE_US = "en_US";
	public static final String TEST_LOCALE_PT = "pt_BR";

	public static final Long TEST_BEING_CODE = 1L;
	public static final String TEST_BEING_NAME = "Test Being";
	
	public static final Long TEST_BEING_CODE_2 = 2L;
	public static final Integer TEST_PAGE_COUNT = 0;
	public static final Integer TEST_PAGE_SIZE = 10;

	public static final Long TEST_BEING_CODE_3 = 3L;

	public static final Long TEST_BEING_CODE_4 = 4L;


	// For broadcast messages
	public static final Integer TEST_PLACE_ID = 999;
	public static final Long TEST_BEING_CODE_5 = 5L;
	public static final Long TEST_BEING_CODE_6 = 6L;
	
	
	// Plain Message
	public static final String TEST_MESSAGE_1 = "Test Message";
	
	// Localized Messages
	public static final String TEST_MESSAGE_2 = "{str:MESSAGE1}";
	public static final String TEST_MESSAGE_2_RESULT_US = "Message in en_US";
	public static final String TEST_MESSAGE_2_RESULT_BR = "Mensagem em pt_BR";
	
	// Placeholder Messages
	public static final String   TEST_MESSAGE_3 = "{str:MESSAGE2}";
	public static final String[] TEST_PARAMETER_3 = new String[] {"One"};
	public static final String   TEST_MESSAGE_3_RESULT = "Message with One parameter";
	
	public static final String   TEST_MESSAGE_4 = "{str:MESSAGE3}";
	public static final String[] TEST_PARAMETER_4 = new String[] {"Text", "Numeric"};
	public static final String   TEST_MESSAGE_4_RESULT = "Message with two(Text and Numeric) parameters";
	
	// Localized Parameter
	public static final String TEST_MESSAGE_5 = "{str:MESSAGE4}";
	public static final String TEST_PARAMETER_5 = "{str:VALUE1}";
	public static final String TEST_MESSAGE_5_RESULT_US = "Message with localized parameter: First.";
	public static final String TEST_MESSAGE_5_RESULT_BR = "Mensagem com parâmetro localizado: Primeiro.";


	// For pagination
	public static final String TEST_MESSAGE_6 = "Test Message %s";

	@MockBean
	private BeingServiceClient beingClient;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private TokenService tokenService;

	
	public String getToken(Long beingCode, String locale) {
		Player playerData = new Player();
		playerData.setUsername(TEST_USERNAME);
		playerData.setPlayerId(TEST_PLAYER_ID);
		playerData.setLocale(locale);
		
		Session sessionData = new Session();
		sessionData.setSessionId(Long.MAX_VALUE);
		sessionData.setBeingCode(beingCode);
		sessionData.setPlayerId(MessageTests.TEST_PLAYER_ID);
		
		String usToken = tokenService.buildToken(MessageTests.TEST_USERNAME, 
				Optional.of(playerData), 
				Optional.of(sessionData));
		
		return usToken;
	}
	
	public HttpEntity<Object> getUsAuthHeader() {
		
		String usToken = getToken(MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US);		
		
		HttpHeaders usHeaders = new HttpHeaders();
		usHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, usToken);
		
		HttpEntity<Object> usAuthEntity = new HttpEntity<Object>(usHeaders);

		return usAuthEntity;
	}
	
	
	public HttpEntity<Object> getBrAuthHeader() {

		
		String brToken = getToken(MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_PT);		
		
		HttpHeaders brHeaders = new HttpHeaders();
		brHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, brToken);
		
		HttpEntity<Object> brAuthEntity = new HttpEntity<Object>(brHeaders);

		return brAuthEntity;
	}
	

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testPlainMessages() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT a message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_1);
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}", HttpMethod.PUT, 
				getUsAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the message
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, getUsAuthHeader(), Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

		Message resultMessage = responseRead.getBody()[0];
		
		assertThat(resultMessage.getSenderCode()).isNull();
		assertThat(resultMessage.getSenderName()).isNull();
		assertThat(resultMessage.getContent()).isEqualTo(MessageTests.TEST_MESSAGE_1);
	}

	@Test
	public void testSender() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT a message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_1);
		urlVariables.put("senderCode", MessageTests.TEST_BEING_CODE);
		urlVariables.put("senderName", MessageTests.TEST_BEING_NAME);
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}&senderName={senderName}&senderCode={senderCode}", 
				HttpMethod.PUT, getUsAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the message
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, getUsAuthHeader(), Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

		Message resultMessage = responseRead.getBody()[0];
		
		assertThat(resultMessage.getSenderCode()).isEqualTo(MessageTests.TEST_BEING_CODE);
		assertThat(resultMessage.getSenderName()).isEqualTo(MessageTests.TEST_BEING_NAME);
	}

	@Test
	public void testUsLocalizedMessage() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT a message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_2);
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}", HttpMethod.PUT, 
				getUsAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the message
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, getUsAuthHeader(), Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

		Message usResultMessage = responseRead.getBody()[0];
		
		assertThat(usResultMessage.getSenderCode()).isNull();
		assertThat(usResultMessage.getSenderName()).isNull();
		assertThat(usResultMessage.getContent()).isEqualTo(MessageTests.TEST_MESSAGE_2_RESULT_US);

		
	}

	@Test
	public void testBrLocalizedMessage() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT a message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_2);
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}", HttpMethod.PUT, 
				getUsAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the message
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, getBrAuthHeader(), Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

		Message brResultMessage = responseRead.getBody()[0];
		
		assertThat(brResultMessage.getSenderCode()).isNull();
		assertThat(brResultMessage.getSenderName()).isNull();
		assertThat(brResultMessage.getContent()).isEqualTo(MessageTests.TEST_MESSAGE_2_RESULT_BR);
	
	}

	
	@Test
	public void testPlaceholderMessages() {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT a message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_3);
		urlVariables.put("parms", String.join(", ", MessageTests.TEST_PARAMETER_3));
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}&parms={parms}", HttpMethod.PUT, 
				getUsAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the message
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, getUsAuthHeader(), Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

		Message resultMessage = responseRead.getBody()[0];
		
		assertThat(resultMessage.getSenderCode()).isNull();
		assertThat(resultMessage.getSenderName()).isNull();
		assertThat(resultMessage.getContent()).isEqualTo(MessageTests.TEST_MESSAGE_3_RESULT);

	}
	
	@Test
	public void testTwoPlaceholderMessage() {

		Map<String, Object> urlVariables = new HashMap<String, Object>();
	
		// PUT another message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_4);
		urlVariables.put("parms", String.join(", ", MessageTests.TEST_PARAMETER_4));
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}&parms={parms}", HttpMethod.PUT, 
				getUsAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the message
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, getUsAuthHeader(), Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

		Message resultMessage = responseRead.getBody()[0];
		
		assertThat(resultMessage.getSenderCode()).isNull();
		assertThat(resultMessage.getSenderName()).isNull();
		assertThat(resultMessage.getContent()).isEqualTo(MessageTests.TEST_MESSAGE_4_RESULT);	
	}

	@Test
	public void testUsLocalizedParameter() {

		Map<String, Object> urlVariables = new HashMap<String, Object>();
	
		// PUT another message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_5);
		urlVariables.put("parms", String.join(", ", MessageTests.TEST_PARAMETER_5));
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}&parms={parms}", HttpMethod.PUT, 
				getUsAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the message
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, getUsAuthHeader(), Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

		Message resultMessage = responseRead.getBody()[0];
		
		assertThat(resultMessage.getSenderCode()).isNull();
		assertThat(resultMessage.getSenderName()).isNull();
		assertThat(resultMessage.getContent()).isEqualTo(MessageTests.TEST_MESSAGE_5_RESULT_US);	
	}

	@Test
	public void testBrLocalizedParameter() {

		Map<String, Object> urlVariables = new HashMap<String, Object>();
	
		// PUT another message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_5);
		urlVariables.put("parms", String.join(", ", MessageTests.TEST_PARAMETER_5));
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}&parms={parms}", HttpMethod.PUT, 
				getBrAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the message
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, getBrAuthHeader(), Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

		Message resultMessage = responseRead.getBody()[0];
		
		assertThat(resultMessage.getSenderCode()).isNull();
		assertThat(resultMessage.getSenderName()).isNull();
		assertThat(resultMessage.getContent()).isEqualTo(MessageTests.TEST_MESSAGE_5_RESULT_BR);	
	}

	@Test
	public void testPagination() {
		
		String usToken = getToken(MessageTests.TEST_BEING_CODE_2, MessageTests.TEST_LOCALE_US);		
			
		HttpHeaders usHeaders = new HttpHeaders();
		usHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, usToken);
		
		HttpEntity<Object> usAuthEntity = new HttpEntity<Object>(usHeaders);
		
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT several messages
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE_2);
		
		for(int i=1;i<=20;i++) {
		
			urlVariables.put("message", String.format(MessageTests.TEST_MESSAGE_6, i));
			
			ResponseEntity<Void> responsePut = restTemplate.exchange(
					"/message/being/{targetCode}?message={message}", HttpMethod.PUT, 
					usAuthEntity, Void.class, urlVariables);
	
			assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		}
		
		// GET the message
		urlVariables.clear();
		urlVariables.put("pageCount", MessageTests.TEST_PAGE_COUNT);
		urlVariables.put("pageSize", MessageTests.TEST_PAGE_SIZE);

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message?pageSize={pageSize}&pageCount={pageCount}", 
				HttpMethod.GET, usAuthEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(MessageTests.TEST_PAGE_SIZE);
	}
	
	@Test
	public void testUnreadMessages() {
		
		String usToken = getToken(MessageTests.TEST_BEING_CODE_3, MessageTests.TEST_LOCALE_US);		
		
		HttpHeaders usHeaders = new HttpHeaders();
		usHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, usToken);
		
		HttpEntity<Object> usAuthEntity = new HttpEntity<Object>(usHeaders);
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT a message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE_3);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_1);
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}", HttpMethod.PUT, 
				usAuthEntity, Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the ONE message recorded
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, usAuthEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

		// Getting ZERO messages (all read)

		responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, usAuthEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(0);
		

		// Getting ONE message again (allMessages = true)
		urlVariables.clear();
		urlVariables.put("allMessages", Boolean.TRUE);

		responseRead = restTemplate.exchange(
				"/message?allMessages={allMessages}", HttpMethod.GET, usAuthEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);
	}


	@Test
	public void testIsolation() {
		
		String anotherBeingToken = getToken(MessageTests.TEST_BEING_CODE_3, MessageTests.TEST_LOCALE_US);		
		
		HttpHeaders anotherBeingHeaders = new HttpHeaders();
		anotherBeingHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, anotherBeingToken);
		
		HttpEntity<Object> anotherBeingAuthEntity = new HttpEntity<Object>(anotherBeingHeaders);
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT a message
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE_4);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_1);
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}?message={message}", HttpMethod.PUT, 
				getUsAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET NONE message with other being
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, anotherBeingAuthEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(0);
		
	}

	@Test
	public void testBroadcastMessage() {
		
		// Configure the mock placeService to return two expected beings when called
		List<Being> testBeingList = new ArrayList<Being>();
		
		Being firstBeing = new Being();
		firstBeing.setCode(MessageTests.TEST_BEING_CODE_5);
		firstBeing.setType(Being.enumBeingType.PLAYABLE);

		Being secondBeing = new Being();
		secondBeing.setCode(MessageTests.TEST_BEING_CODE_6);
		secondBeing.setType(Being.enumBeingType.PLAYABLE);
		
		testBeingList.add(firstBeing);
		testBeingList.add(secondBeing);
		
		given(this.beingClient.getAllFromPlace("aforgotten", MessageTests.TEST_PLACE_ID))
		.willReturn(testBeingList);

		String firstToken = getToken(MessageTests.TEST_BEING_CODE_5, MessageTests.TEST_LOCALE_US);		
		
		HttpHeaders firstHeaders = new HttpHeaders();
		firstHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, firstToken);
		
		HttpEntity<Object> firstAuthEntity = new HttpEntity<Object>(firstHeaders);		

		String secondToken = getToken(MessageTests.TEST_BEING_CODE_6, MessageTests.TEST_LOCALE_US);
		
		HttpHeaders secondHeaders = new HttpHeaders();
		secondHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, secondToken);
		
		HttpEntity<Object> secondAuthEntity = new HttpEntity<Object>(secondHeaders);		
		
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT a message
		urlVariables.put("placeCode", MessageTests.TEST_PLACE_ID);
		urlVariables.put("message", MessageTests.TEST_MESSAGE_1);
		
		ResponseEntity<Void> responsePut = restTemplate.exchange(
				"/message/place/{placeCode}?message={message}", HttpMethod.PUT, 
				getUsAuthHeader(), Void.class, urlVariables);

		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// GET the message with one being
		urlVariables.clear();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, firstAuthEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);
	

		// GET the message with other
		urlVariables.clear();

		responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, secondAuthEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(1);

	}
}
