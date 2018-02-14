package com.jpinfo.mudengine.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.security.TokenService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class MessageTests {
	
	public static final String TEST_USERNAME = "username";
	public static final Long TEST_PLAYER_ID = 1L;
	
	public static final String TEST_LOCALE_US = "en_US";
	public static final String TEST_LOCALE_PT = "pt_BR";

	public static final Long TEST_BEING_CODE = 1L;

	@Autowired
	private TestRestTemplate restTemplate;
	

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testMessages() {
		
		// Creating the authentication token
		String usToken = TokenService.buildToken(
				MessageTests.TEST_USERNAME, 
				MessageTests.TEST_PLAYER_ID, 
				MessageTests.TEST_LOCALE_US,
				MessageTests.TEST_BEING_CODE);
		
		String brToken = TokenService.buildToken(
				MessageTests.TEST_USERNAME, 
				MessageTests.TEST_PLAYER_ID, 
				MessageTests.TEST_LOCALE_PT, 
				MessageTests.TEST_BEING_CODE);
		
		HttpHeaders usHeaders = new HttpHeaders();
		usHeaders.add(TokenService.HEADER_TOKEN, usToken);

		HttpHeaders brHeaders = new HttpHeaders();
		usHeaders.add(TokenService.HEADER_TOKEN, brToken);
		
		HttpEntity<Object> usAuthEntity = new HttpEntity<Object>(usHeaders);
		HttpEntity<Object> brAuthEntity = new HttpEntity<Object>(brHeaders);
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, usAuthEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(5);
		
		// First: plain Message
		Message firstMessage = responseRead.getBody()[0];
		
		assertThat(firstMessage.getInsertTurn()).isEqualTo(10);
		assertThat(firstMessage.getSenderCode()).isNull();
		assertThat(firstMessage.getSenderName()).isNull();
		//assertThat(firstMessage.getMessage()).isEqualTo(expected);
		
		// Second: localized Message
		Message secondMessage = responseRead.getBody()[1];
		
		assertThat(secondMessage.getInsertTurn()).isEqualTo(11);
		assertThat(secondMessage.getSenderCode()).isNull();
		assertThat(secondMessage.getSenderName()).isNull();
		//assertThat(secondMessage.getMessage()).isEqualTo(expected);
		
		// Third: One Placeholder Message
		Message thirdMessage = responseRead.getBody()[2];
		
		assertThat(thirdMessage.getInsertTurn()).isEqualTo(12);
		assertThat(thirdMessage.getSenderCode()).isNull();
		assertThat(thirdMessage.getSenderName()).isNull();
		//assertThat(thirdMessage.getMessage()).isEqualTo(expected);
		
		// Fourth: Two Placeholders Message
		Message fourthMessage = responseRead.getBody()[3];
		
		assertThat(fourthMessage.getInsertTurn()).isEqualTo(13);
		assertThat(fourthMessage.getSenderCode()).isNull();
		assertThat(fourthMessage.getSenderName()).isNull();
		//assertThat(fourthMessage.getMessage()).isEqualTo(expected);
		
		// Fifth: Localized Parameter
		Message fifthMessage = responseRead.getBody()[4];
		
		assertThat(fifthMessage.getInsertTurn()).isEqualTo(14);
		assertThat(fifthMessage.getSenderCode()).isNull();
		assertThat(fifthMessage.getSenderName()).isNull();
		//assertThat(fifthMessage.getMessage()).isEqualTo(expected);

		
		ResponseEntity<Message[]> responseReadBr = restTemplate.exchange(
			"/message", HttpMethod.GET, brAuthEntity, Message[].class, urlVariables);

		assertThat(responseReadBr.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseReadBr.getBody().length).isEqualTo(5);
		
		// Second: localized Message
		Message secondMessageBr = responseReadBr.getBody()[1];
		
		assertThat(secondMessageBr.getInsertTurn()).isEqualTo(11);
		assertThat(secondMessageBr.getSenderCode()).isNull();
		assertThat(secondMessageBr.getSenderName()).isNull();
		//assertThat(secondMessageBr.getMessage()).isEqualTo(expected);
		
		// Third: One Placeholder Message
		Message thirdMessageBr = responseReadBr.getBody()[2];
		
		assertThat(thirdMessageBr.getInsertTurn()).isEqualTo(12);
		assertThat(thirdMessageBr.getSenderCode()).isNull();
		assertThat(thirdMessageBr.getSenderName()).isNull();
		//assertThat(thirdMessageBr.getMessage()).isEqualTo(expected);
		
		// Fourth: Two Placeholders Message
		Message fourthMessageBr = responseReadBr.getBody()[3];
		
		assertThat(fourthMessageBr.getInsertTurn()).isEqualTo(13);
		assertThat(fourthMessageBr.getSenderCode()).isNull();
		assertThat(fourthMessageBr.getSenderName()).isNull();
		//assertThat(fourthMessageBr.getMessage()).isEqualTo(expected);
		
		// Fifth: Localized Parameter
		Message fifthMessageBr = responseReadBr.getBody()[4];
		
		assertThat(fifthMessageBr.getInsertTurn()).isEqualTo(14);
		assertThat(fifthMessageBr.getSenderCode()).isNull();
		assertThat(fifthMessageBr.getSenderName()).isNull();
		//assertThat(fifthMessageBr.getMessage()).isEqualTo(expected);
		
	}
}
