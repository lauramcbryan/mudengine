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
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.utils.ApiErrorMessage;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.utils.CommonConstants;

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
		
		try {
			ResponseEntity<Player> response = 
					restTemplate.exchange(apiEndpoint + "/player/{username}", 
							HttpMethod.GET, getEmptyHttpEntity(authToken), Player.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			
			handleError(e, "Player");
		}

		return result;
	}

	@Override
	public void registerPlayer(String username, String email, String locale) throws ClientException {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", username);
		urlVariables.put("email", email);
		urlVariables.put("locale", locale);
		
		try {
			
			restTemplate.exchange(
					apiEndpoint + "/player/{username}?email={email}&locale={locale}", 
					HttpMethod.PUT, getEmptyHttpEntity(), Player.class, urlVariables);
			
		} catch(RestClientResponseException e) {
			
			handleError(e, "Player");
		}
	}

	@Override
	public Player updatePlayerDetails(String authToken, Player playerData) throws ClientException {
		
		Player result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", playerData.getUsername());
		
		HttpEntity<Player> playerRequest = new HttpEntity<Player>(playerData, getAuthHeaders(authToken));
		
		try {
			ResponseEntity<Player> response = restTemplate.exchange(
					apiEndpoint + "/player/{username}", 
					HttpMethod.POST, playerRequest, Player.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			
			handleError(e, "Player");
		}
		
		return result;
	}

	@Override
	public void setPlayerPassword(String username, String activationCode, String newPassword) throws ClientException {
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", username);
		urlVariables.put("activationCode", activationCode);
		urlVariables.put("newPassword", newPassword);
		
		try {
			restTemplate.exchange(
					apiEndpoint + "/player/{username}/password?activationCode={activationCode}&newPassword={newPassword}", 
					HttpMethod.POST, null, String.class, urlVariables);
			
		} catch(RestClientResponseException e) {
			handleError(e, "Player");
		}
	}

	@Override
	public Session setActiveBeing(String authToken, String username, Long beingCode) throws ClientException {
		
		Session result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", username);
		urlVariables.put("beingCode", beingCode);
		
		try {
		
			ResponseEntity<Session> response = restTemplate.exchange(
					apiEndpoint + "/player/{username}/password?activationCode={activationCode}&newPassword={newPassword}", 
					HttpMethod.POST, null, Session.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			
			handleError(e, "Being");
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
		
		try {
		
			ResponseEntity<Session> response = restTemplate.exchange(
					apiEndpoint + "/player/{username}/being?beingClass={beingClass}&beingName={beingName}&worldName={worldName}&placeCode={placeCode}", 
					HttpMethod.PUT, getEmptyHttpEntity(authToken), Session.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e, "Being Class");
		}

		return result;
	}

	@Override
	public Session destroyBeing(String authToken, String username, Long beingCode) throws ClientException {
		
		Session result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("username", username);
		urlVariables.put("beingCode", beingCode);
		
		try {
			ResponseEntity<Session> response = restTemplate.exchange(
					apiEndpoint + "/{username}/session/being/{beingCode}", 
					HttpMethod.DELETE, getEmptyHttpEntity(authToken), Session.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e, "Being");
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
		
		try {
		
			ResponseEntity<Session> response = restTemplate.exchange(
					apiEndpoint + "/player/{username}/session?password={password}&clientType={clientType}&ipAddress={ipAddress}", 
					HttpMethod.PUT, getEmptyHttpEntity(), Session.class, urlVariables);
			
			result = response.getHeaders().getFirst(CommonConstants.AUTH_TOKEN_HEADER);

		} catch(RestClientResponseException e) {
			handleError(e, "Session");
		}
		
		return result;
	}
	
	@Override
	public Session getSession(String authToken, String username) throws ClientException {

		Session result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("username", username);

		try {
			ResponseEntity<Session> response = restTemplate.exchange(
					apiEndpoint + "/player/{username}/session", 
					HttpMethod.GET, getEmptyHttpEntity(authToken), Session.class, urlVariables);

			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e, "Session");
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
		
		try {
			ResponseEntity<Action> response = restTemplate.exchange(
					apiEndpoint + "/action/{verb}?actorCode={actorCode}&mediatorCode={mediatorCode}&mediatorType={mediatorType}&targetCode={targetCoide}&targetType={targetType}", 
					HttpMethod.PUT, getEmptyHttpEntity(), Action.class, urlVariables);

			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e, "Command");
		}
		
		return result;
	}

	@Override
	public Being getBeing(String authToken, Long beingCode) throws ClientException {
		
		Being result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("beingCode", beingCode);

		try {
			ResponseEntity<Being> response = restTemplate.exchange(apiEndpoint + "/being/{beingCode}", 
					HttpMethod.GET, getEmptyHttpEntity(authToken), Being.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e, "Being");
		}

		return result;
	}

	@Override
	public Item getItem(String authToken, Long itemId)  throws ClientException {
		
		Item result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("itemId", itemId);

		try {
			ResponseEntity<Item> response = restTemplate.exchange(apiEndpoint + "/item/{itemId}", 
					HttpMethod.GET, getEmptyHttpEntity(authToken), Item.class, urlVariables);

			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e, "Item");
		}

		return result;
	}

	@Override
	public Place getPlace(String authToken, Integer placeId) throws ClientException {
		
		Place result = null;
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("placeId", placeId);

		try {
			ResponseEntity<Place> response = restTemplate.exchange(apiEndpoint + "/place/{placeId}", 
					HttpMethod.GET, getEmptyHttpEntity(authToken), Place.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e, "Place");
		}
			
		return result;
	}

	@Override
	public List<Message> getMessages(String authToken) {

		List<Message> returnList = new ArrayList<Message>();
		
		RestTemplate restTemplate = new RestTemplate();

		try {
			ResponseEntity<Message[]> responseRead = restTemplate.exchange(
					apiEndpoint + "/message", 
					HttpMethod.GET, getEmptyHttpEntity(authToken), 
					Message[].class, new HashMap<String, Object>());
			
			returnList = (List<Message>)Arrays.asList(responseRead.getBody());

		} catch(RestClientResponseException e) {
			System.out.println(e.getResponseBodyAsString());
		}
		
		return returnList;
	}

	private HttpHeaders getAuthHeaders(String authToken) {
		
		HttpHeaders clientHeaders = new HttpHeaders();
		clientHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, authToken);
		
		return clientHeaders;
	}
	
	private HttpEntity<Object> getEmptyHttpEntity(String authToken) {
		
		return new HttpEntity<Object>(getAuthHeaders(authToken));
	}
	
	private HttpEntity<Object> getEmptyHttpEntity() {
		
		return new HttpEntity<Object>(new HttpHeaders());
	}
	
	private void handleError(RestClientResponseException exception, String entity) throws ClientException {

		try {
			ApiErrorMessage restError = ApiErrorMessage.build(exception.getResponseBodyAsString());
			
			switch(restError.getStatus()) {
			
				case 404: {
					throw new ClientException(entity + "not found");
				}
				case 400: {
					
					throw new ClientException(restError.getMessage());
				}			
				case 403: {
					
					String errorMessage = restError.getMessage();
					
					throw new ClientException(errorMessage);
					
				}
				default:
					throw new ClientException("Error trying to access the service");
			}
		} catch(ClientException e) {
			throw e;
		} catch(Exception e) {
			
			e.printStackTrace();
			throw new ClientException("Error trying to access the service");
		}
	}
	
}
