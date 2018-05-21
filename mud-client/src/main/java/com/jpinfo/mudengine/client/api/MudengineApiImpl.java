package com.jpinfo.mudengine.client.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

@Component
public class MudengineApiImpl implements MudengineApi {
	
	@Value("${api.endpoint}")
	private String apiEndpoint;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Override
	public Player getPlayerDetails(String authToken, String username) throws ClientException {
		
		Player result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", username);
		
		ResponseEntity<Player> responseGet = restTemplate.exchange(apiEndpoint + "/player/{username}", 
				HttpMethod.GET, getEmptyHttpEntity(authToken), Player.class, urlVariables);
		
		switch(responseGet.getStatusCode()) {
			
			case OK: {
				
				result = responseGet.getBody();
				break;
			}
			case NOT_FOUND: {

				throw new ClientException("Player not found");
				
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}

		return result;
	}

	@Override
	public void registerPlayer(String username, String email, String locale) throws ClientException {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", username);
		urlVariables.put("email", email);
		urlVariables.put("locale", locale);
		
		ResponseEntity<Player> responseGet = restTemplate.exchange(
				apiEndpoint + "/player/{username}?email={email}&locale={locale}", 
				HttpMethod.GET, getEmptyHttpEntity(), Player.class, urlVariables);
		
		switch(responseGet.getStatusCode()) {
			
			case BAD_REQUEST: {
				
				throw new ClientException(responseGet.getStatusCode().getReasonPhrase());
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}
	}

	@Override
	public Player updatePlayerDetails(String authToken, Player playerData) throws ClientException {
		
		Player result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", playerData.getUsername());
		
		HttpEntity<Player> playerRequest = new HttpEntity<Player>(playerData, getAuthHeaders(authToken));
		
		ResponseEntity<Player> response = restTemplate.exchange(
				apiEndpoint + "/player/{username}", 
				HttpMethod.POST, playerRequest, Player.class, urlVariables);
		
		switch(response.getStatusCode()) {
		
			case OK: {
				
				result = response.getBody();
				
				break;
			}
			
			case BAD_REQUEST: {
				
				throw new ClientException(response.getStatusCode().getReasonPhrase());
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}
		
		return result;
	}

	@Override
	public void setPlayerPassword(String username, String activationCode, String newPassword) throws ClientException {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", username);
		urlVariables.put("activationCode", activationCode);
		urlVariables.put("newPassword", newPassword);
		
		ResponseEntity<String> changePasswordResponse = restTemplate.exchange(
				apiEndpoint + "/player/{username}/password?activationCode={activationCode}&newPassword={newPassword}", 
				HttpMethod.POST, null, String.class, urlVariables);
		
		switch(changePasswordResponse.getStatusCode()) {
		
			case BAD_REQUEST: {
				
				throw new ClientException(changePasswordResponse.getStatusCode().getReasonPhrase());
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}
	}

	@Override
	public Session setActiveBeing(String authToken, String username, Long beingCode) throws ClientException {
		
		Session result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", username);
		urlVariables.put("beingCode", beingCode);
		
		ResponseEntity<Session> response = restTemplate.exchange(
				apiEndpoint + "/player/{username}/password?activationCode={activationCode}&newPassword={newPassword}", 
				HttpMethod.POST, null, Session.class, urlVariables);
		
		switch(response.getStatusCode()) {
		
			case OK: {
				
				result = response.getBody();
				break;
			}
		
			case BAD_REQUEST: {
				
				throw new ClientException(response.getStatusCode().getReasonPhrase());
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}

		return result;
	}

	@Override
	public Session createBeing(String authToken, String username, String beingClass, String beingName, String worldName,
			Integer placeCode) throws ClientException {
		
		Session result = null;

		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("username", username);
		urlVariables.put("beingClass", beingClass);
		urlVariables.put("beingName", beingName);
		urlVariables.put("worldName", worldName);
		urlVariables.put("placeCode", placeCode);
		
		ResponseEntity<Session> response = restTemplate.exchange(
				apiEndpoint + "/player/{username}/being?beingClass={beingClass}&beingName={beingName}&worldName={worldName}&placeCode={placeCode}", 
				HttpMethod.PUT, getEmptyHttpEntity(authToken), Session.class, urlVariables);
		
		switch(response.getStatusCode()) {
		
			case OK: {
				
				result = response.getBody();
				break;
			}
		
			case BAD_REQUEST: {
				
				throw new ClientException(response.getStatusCode().getReasonPhrase());
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}

		return result;
	}

	@Override
	public Session destroyBeing(String authToken, String username, Long beingCode) throws ClientException {
		
		Session result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("username", username);
		urlVariables.put("beingCode", beingCode);
		
		ResponseEntity<Session> response = restTemplate.exchange(
				apiEndpoint + "/{username}/session/being/{beingCode}", 
				HttpMethod.DELETE, getEmptyHttpEntity(authToken), Session.class, urlVariables);

		switch(response.getStatusCode()) {
		
			case OK: {
				
				result = response.getBody();
				break;
			}
		
			case BAD_REQUEST: {
				
				throw new ClientException(response.getStatusCode().getReasonPhrase());
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}
		
		return result;
	}

	@Override
	public String createSession(String username, String password, String clientType, String ipAddress) throws ClientException {
		
		String result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("username", username);
		urlVariables.put("password", password);
		urlVariables.put("clientType", clientType);
		urlVariables.put("ipAddress", ipAddress);
		
		ResponseEntity<Session> response = restTemplate.exchange(
				apiEndpoint + "/player/{username}/session?password={password}&clientType={clientType}&ipAddress={ipAddress}", 
				HttpMethod.PUT, getEmptyHttpEntity(), Session.class, urlVariables);

		switch(response.getStatusCode()) {
		
			case OK: {
				
				result = response.getHeaders().getFirst(ClientHelper.HEADER_TOKEN);
				break;
			}
		
			case BAD_REQUEST: {
				
				throw new ClientException(response.getStatusCode().getReasonPhrase());
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}
		
		return result;
	}
	
	@Override
	public Session getSession(String authToken, String username) throws ClientException {

		Session result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", username);
		
		ResponseEntity<Session> response = restTemplate.exchange(
				apiEndpoint + "/player/{username}/session", 
				HttpMethod.GET, getEmptyHttpEntity(authToken), Session.class, urlVariables);

		switch(response.getStatusCode()) {
		
			case OK: {
				
				result = response.getBody();
				break;
			}
		
			case BAD_REQUEST: {
				
				throw new ClientException(response.getStatusCode().getReasonPhrase());
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}
		
		return result;
		
	}

	@Override
	public Action insertCommand(String authToken, String verb, Long actorCode, Optional<String> mediatorCode,
			Optional<String> mediatorType, String targetCode, String targetType) throws ClientException {
		
		Action result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("actorCode", actorCode);
		
		mediatorCode.ifPresent(d -> urlVariables.put("mediatorCode", d));
		mediatorType.ifPresent(d -> urlVariables.put("mediatorType", d));
		
		urlVariables.put("targetCode", targetCode);
		urlVariables.put("targetType", targetType);
		
		ResponseEntity<Action> response = restTemplate.exchange(
				apiEndpoint + "/action/{verb}?actorCode={actorCode}&mediatorCode={mediatorCode}&mediatorType={mediatorType}&targetCode={targetCoide}&targetType={targetType}", 
				HttpMethod.PUT, getEmptyHttpEntity(), Action.class, urlVariables);

		switch(response.getStatusCode()) {
		
			case OK: {
				
				result = response.getBody();
				break;
			}
		
			case BAD_REQUEST: {
				
				throw new ClientException(response.getStatusCode().getReasonPhrase());
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}
		
		return result;
	}

	@Override
	public Being getBeing(String authToken, Long beingCode) throws ClientException {
		
		Being result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("beingCode", beingCode);
		
		ResponseEntity<Being> responseGet = restTemplate.exchange(apiEndpoint + "/being/{beingCode}", 
				HttpMethod.GET, getEmptyHttpEntity(authToken), Being.class, urlVariables);
		
		switch(responseGet.getStatusCode()) {
			
			case OK: {
				
				result = responseGet.getBody();
				break;
			}
			case NOT_FOUND: {

				throw new ClientException("Being not found");
				
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}

		return result;
	}

	@Override
	public Item getItem(String authToken, Long itemId)  throws ClientException {
		
		Item result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("itemId", itemId);
		
		ResponseEntity<Item> responseGet = restTemplate.exchange(apiEndpoint + "/item/{itemId}", 
				HttpMethod.GET, getEmptyHttpEntity(authToken), Item.class, urlVariables);
		
		switch(responseGet.getStatusCode()) {
			
			case OK: {
				
				result = responseGet.getBody();
				break;
			}
			case NOT_FOUND: {

				throw new ClientException("Item not found");
				
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}

		return result;
	}

	@Override
	public Place getPlace(String authToken, Integer placeId) throws ClientException {
		
		Place result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("placeId", placeId);
		
		ResponseEntity<Place> responseGet = restTemplate.exchange(apiEndpoint + "/place/{placeId}", 
				HttpMethod.GET, getEmptyHttpEntity(authToken), Place.class, urlVariables);
		
		switch(responseGet.getStatusCode()) {
			
			case OK: {
				
				result = responseGet.getBody();
				break;
			}
			case NOT_FOUND: {

				throw new ClientException("Place not found");
				
			}
			default: {
				throw new ClientException("Error trying to access the service");
			}
		}
			
		return result;
	}

	@Override
	public List<Message> getMessages(String authToken) {

		List<Message> returnList = new ArrayList<Message>();
		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<Message[]> responseRead = restTemplate.exchange(
				apiEndpoint + "/message", 
				HttpMethod.GET, getEmptyHttpEntity(authToken), 
				Message[].class, new HashMap<String, Object>());

		if (responseRead.getStatusCode().is2xxSuccessful()) {
			
			// Returning all the messages received
			returnList = (List<Message>)Arrays.asList(responseRead.getBody());
		}
		
		return returnList;
	}

	private HttpHeaders getAuthHeaders(String authToken) {
		
		HttpHeaders clientHeaders = new HttpHeaders();
		clientHeaders.add(ClientHelper.HEADER_TOKEN, authToken);
		
		return clientHeaders;
	}
	
	private HttpEntity<Object> getEmptyHttpEntity(String authToken) {
		
		return new HttpEntity<Object>(getAuthHeaders(authToken));
	}
	
	private HttpEntity<Object> getEmptyHttpEntity() {
		
		return new HttpEntity<Object>(new HttpHeaders());
	}
	
}
