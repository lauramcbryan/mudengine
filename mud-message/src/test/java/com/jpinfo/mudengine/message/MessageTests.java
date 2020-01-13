package com.jpinfo.mudengine.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

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
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.message.client.BeingServiceClient;
import com.jpinfo.mudengine.message.fixture.MessageProcessor;
import com.jpinfo.mudengine.message.fixture.MessageTemplates;
import com.jpinfo.mudengine.message.model.MudMessage;
import com.jpinfo.mudengine.message.model.MudMessageLocale;
import com.jpinfo.mudengine.message.model.pk.MudMessageLocalePK;
import com.jpinfo.mudengine.message.repository.MudMessageLocaleRepository;
import com.jpinfo.mudengine.message.repository.MudMessageRepository;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8"})
public class MessageTests {
	
	private static final String TEST_WORLD = "aforgotten";
	
	private static final String TEST_USERNAME = "username";
	private static final Long TEST_PLAYER_ID = 1L;
	
	private static final String TEST_LOCALE_US = "en_US";

	private static final Long TEST_BEING_CODE = 1L;
	
	private static final Long TEST_BEING_CODE_2 = 2L;
	private static final Integer TEST_PAGE_COUNT = 1;
	private static final Integer TEST_PAGE_SIZE = 10;

	private static final Long TEST_BEING_CODE_3 = 3L;


	// For broadcast messages
	private static final Integer TEST_PLACE_ID = 999;
	
	
	// Localized Messages
	private static final String TEST_MESSAGE_2_RESULT_US = "Message in en_US";

	@MockBean
	private BeingServiceClient beingClient;

	@MockBean
	private MudMessageLocaleRepository localeRepository;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private MudMessageRepository repository;

	
	private String getToken(Long beingCode, String locale) throws IOException {
		Player playerData = new Player();
		playerData.setUsername(TEST_USERNAME);
		playerData.setPlayerId(TEST_PLAYER_ID);
		playerData.setLocale(locale);
		
		Session sessionData = new Session();
		sessionData.setSessionId(Long.MAX_VALUE);
		sessionData.setBeingCode(beingCode);
		sessionData.setPlayerId(MessageTests.TEST_PLAYER_ID);
		sessionData.setCurWorldName(MessageTests.TEST_WORLD);
		
		String usToken = tokenService.buildToken(MessageTests.TEST_USERNAME, 
				playerData, 
				sessionData);
		
		return usToken;
	}
	

	@PostConstruct
	public void setup() {
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.message.fixture");
	}
	
	private HttpEntity<Object> getRequestEntity(Long beingCode, String locale) throws IOException {
		
		String usToken = getToken(beingCode, locale);		
		
		HttpHeaders usHeaders = new HttpHeaders();
		usHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, usToken);
		
		return new HttpEntity<Object>(usHeaders);
	}
	
	private HttpEntity<MessageRequest> getRequestEntity(Long beingCode, String locale, MessageRequest request) throws IOException {
		
		String usToken = getToken(beingCode, locale);		
		
		HttpHeaders usHeaders = new HttpHeaders();
		usHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, usToken);
		
		return new HttpEntity<MessageRequest>(request, usHeaders);
	}	
		
	@Test
	public void testPutMessage() throws IOException {

		// Creating the request object
		MessageRequest msgRequest = 
				Fixture.from(MessageRequest.class).gimme(MessageTemplates.VALID);
	
		HttpEntity<MessageRequest> requestEntity = getRequestEntity(
				MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US, msgRequest);
		
		// Preparing the parameters
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		
		// Launching the service
		ResponseEntity<Long> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}", HttpMethod.PUT, 
				requestEntity, Long.class, urlVariables);
		
		// Check the service response code
		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		// Check if message is recorded in database
		MudMessage responseMessage = repository.findById(responsePut.getBody())
				.orElse(null);

		// Check if message exists
		assertThat(responseMessage).isNotNull();
		
		// Check message fields
		assertThat(responseMessage.getBeingCode()).isEqualTo(MessageTests.TEST_BEING_CODE);
		assertThat(responseMessage.getMessageKey()).isEqualTo(msgRequest.getMessageKey());
		
		// Check if all args passed are persisted in database
		for(String curArg: msgRequest.getArgs()) {
		
			assertThat(responseMessage.getParms().stream()
				.anyMatch(d -> d.getValue().equals(curArg))
				).isTrue();
		}
	}
	
	@Test
	public void testBroadcastMessage() throws IOException {

		// Creating the request object
		MessageRequest msgRequest = 
				Fixture.from(MessageRequest.class).gimme(MessageTemplates.VALID);
	
		HttpEntity<MessageRequest> requestEntity = getRequestEntity(
				MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US, msgRequest);
		
		// Preparing the parameters
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("placeCode", MessageTests.TEST_PLACE_ID);
		
		// Creating the beings in place
		List<Being> beingsInPlace = Fixture.from(Being.class).gimme(3, MessageTemplates.VALID);
		
		given(beingClient.getAllFromPlace(MessageTests.TEST_WORLD, MessageTests.TEST_PLACE_ID))
			.willReturn(beingsInPlace);
		
		// Launching the service
		ResponseEntity<Long[]> responsePut = restTemplate.exchange(
				"/message/place/{placeCode}", HttpMethod.PUT, 
				requestEntity, Long[].class, urlVariables);
		
		// Check the service response code
		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		// Check if message is recorded in database
		for(Long curMessageId: responsePut.getBody()) {
		
			MudMessage responseMessage = repository.findById(curMessageId)
					.orElse(null);

			// Check if message exists
			assertThat(responseMessage).isNotNull();
			
			// Check message fields
			assertThat(beingsInPlace.stream()
					.anyMatch(d -> d.getCode().equals(responseMessage.getBeingCode()))
					).isTrue();
			
			assertThat(responseMessage.getMessageKey()).isEqualTo(msgRequest.getMessageKey());
			
			// Check if all args passed are persisted in database
			for(String curArg: msgRequest.getArgs()) {
			
				assertThat(responseMessage.getParms().stream()
					.anyMatch(d -> d.getValue().equals(curArg))
					).isTrue();
			}
		}
	}

	@Test
	public void testSender() throws IOException {
		
		// Creating the request object
		MessageRequest msgRequest = 
				Fixture.from(MessageRequest.class).gimme(MessageTemplates.VALID);
	
		HttpEntity<MessageRequest> requestEntity = getRequestEntity(
				MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US, msgRequest);
		
		// Preparing the parameters
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		
		// Launching the service
		ResponseEntity<Long> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}", HttpMethod.PUT, 
				requestEntity, Long.class, urlVariables);
		
		// Check the service response code
		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		// Check if message is recorded in database
		MudMessage responseMessage = repository.findById(responsePut.getBody())
				.orElse(null);

		// Check if message exists
		assertThat(responseMessage).isNotNull();
		
		// Check message fields (only the ones that matter for this test)
		assertThat(responseMessage.getSenderCode()).isEqualTo(msgRequest.getSenderCode());
		assertThat(responseMessage.getSenderName()).isEqualTo(msgRequest.getSenderName());
	}

	@Test
	public void testChangedEntities() throws IOException {
		
		// Creating the request object
		MessageRequest msgRequest = 
				Fixture.from(MessageRequest.class).gimme(MessageTemplates.ENTITIES);
	
		HttpEntity<MessageRequest> requestEntity = getRequestEntity(
				MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US, msgRequest);
		
		// Preparing the parameters
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		
		// Launching the service
		ResponseEntity<Long> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}", HttpMethod.PUT, 
				requestEntity, Long.class, urlVariables);
		
		// Check the service response code
		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		// Check if message is recorded in database
		MudMessage responseMessage = repository.findById(responsePut.getBody())
				.orElse(null);

		// Check if message exists
		assertThat(responseMessage).isNotNull();
		
		// Check if all entities passed are persisted in database
		msgRequest.getChangedEntities().stream()
			.forEach(d -> 
				assertThat(responseMessage.getEntities().stream()
						.anyMatch(e -> (
								d.getEntityId().equals(e.getId().getEntityId()) &&
								d.getEntityType().toString().equals(e.getId().getEntityType())
									)
								)
						).isTrue()
			);
	}
	

	@Test
	public void testLocalizedMessage() throws IOException {
		
		// Creating the request object
		MessageRequest msgRequest = 
				Fixture.from(MessageRequest.class).gimme(MessageTemplates.VALID);
	
		HttpEntity<MessageRequest> requestEntity = getRequestEntity(
				MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US, msgRequest);
		
		// Preparing the parameters
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);

		// Creating the mocked locale message
		MudMessageLocale localeMessage = new MudMessageLocale();
		localeMessage.setPk(new MudMessageLocalePK(msgRequest.getMessageKey(),TEST_LOCALE_US));
		localeMessage.setMessageText(TEST_MESSAGE_2_RESULT_US);

		// Instructing the mocked repository to return the correct message
		given(localeRepository.findById(localeMessage.getPk()))
			.willReturn(Optional.of(localeMessage));
		
		// Launching the service
		ResponseEntity<Long> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}", HttpMethod.PUT, 
				requestEntity, Long.class, urlVariables);
		
		// Check the service response code
		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		// Creating the request entities:
		
		// US profile request
		HttpEntity<Object> usRequestEntity = getRequestEntity(
				MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US);

		// Reading the message with a US profile token
		ResponseEntity<Message[]> usResponse = restTemplate.exchange(
				"/message", HttpMethod.GET, usRequestEntity, Message[].class, urlVariables);
		
		// Check the service response code
		assertThat(usResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// Check if the message is localized according to US profile
		assertThat(usResponse.getBody()[0].getContent()).isEqualTo(MessageTests.TEST_MESSAGE_2_RESULT_US);		
	}
	
	

	@Test
	public void testPagination() throws IOException {

		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// PUT several messages
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		
		for(int i=1;i<=20;i++) {
			
			MessageRequest msgRequest = 
					Fixture.from(MessageRequest.class).gimme(MessageTemplates.VALID);
			
			HttpEntity<MessageRequest> requestEntity =
					getRequestEntity(MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US, msgRequest);
		
			ResponseEntity<Long> responsePut = restTemplate.exchange(
					"/message/being/{targetCode}", HttpMethod.PUT, 
					requestEntity, Long.class, urlVariables);
	
			assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		}
		
		// GET the messages
		urlVariables.clear();
		urlVariables.put("pageCount", MessageTests.TEST_PAGE_COUNT);
		urlVariables.put("pageSize", MessageTests.TEST_PAGE_SIZE);
		
		HttpEntity<Object> emptyRequestEntity =
				getRequestEntity(MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US);

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message?pageSize={pageSize}&pageCount={pageCount}", 
				HttpMethod.GET, emptyRequestEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(MessageTests.TEST_PAGE_SIZE);
	}
	
	@Test
	public void testUnreadMessages() throws IOException {
		
		// Do not exceed the page size while doing this test, or the pagination
		// will mess up with your test logic
		List<MudMessage> dbMessages = Fixture.from(MudMessage.class)
				.uses(new MessageProcessor(MessageTests.TEST_BEING_CODE_3))
				.gimme(TEST_PAGE_SIZE, MessageTemplates.VALID);
		
		// Putting the messages in database
		repository.saveAll(dbMessages);
		
		// Reading
		long unreadCount = dbMessages.stream().filter(d -> !d.getReadFlag()).count();
		
		HttpEntity<Object> emptyRequestEntity =  getRequestEntity(MessageTests.TEST_BEING_CODE_3, MessageTests.TEST_LOCALE_US);
		
		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, emptyRequestEntity, Message[].class, new HashMap<String, Object>());
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(unreadCount);

		// Getting ZERO messages (all read)
		responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, emptyRequestEntity, Message[].class, new HashMap<String, Object>());
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(0);
		

		// Getting ALL messages again (allMessages = true)
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("allMessages", Boolean.TRUE);

		responseRead = restTemplate.exchange(
				"/message?allMessages={allMessages}", HttpMethod.GET, emptyRequestEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(dbMessages.size());
	}


	@Test
	public void testIsolation() throws IOException {
		
		// Creating the request object
		MessageRequest msgRequest = 
				Fixture.from(MessageRequest.class).gimme(MessageTemplates.VALID);
	
		HttpEntity<MessageRequest> requestEntity = getRequestEntity(
				MessageTests.TEST_BEING_CODE, MessageTests.TEST_LOCALE_US, msgRequest);
		
		// Preparing the parameters
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("targetCode", MessageTests.TEST_BEING_CODE);
		
		// Launching the service
		ResponseEntity<Long> responsePut = restTemplate.exchange(
				"/message/being/{targetCode}", HttpMethod.PUT, 
				requestEntity, Long.class, urlVariables);
		
		// Check the service response code
		assertThat(responsePut.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		HttpEntity<Object> anotherBeingRequestEntity = getRequestEntity(MessageTests.TEST_BEING_CODE_2, MessageTests.TEST_LOCALE_US);
		

		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				"/message", HttpMethod.GET, anotherBeingRequestEntity, Message[].class, urlVariables);
		
		assertThat(responseRead.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseRead.getBody().length).isEqualTo(0);
		
	}
}
