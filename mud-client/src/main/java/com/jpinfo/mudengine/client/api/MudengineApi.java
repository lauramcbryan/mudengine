package com.jpinfo.mudengine.client.api;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

@Component
public interface MudengineApi {
	
	Player getPlayerDetails(String authToken, String username) throws ClientException;

	void registerPlayer(String username, String email, String locale) throws ClientException;

	Player updatePlayerDetails(String authToken, Player playerData) throws ClientException;
	
	void setPlayerPassword(String username, String activationCode, String newPassword) throws ClientException;

	Session setActiveBeing(String authToken, String username, Long beingCode) throws ClientException;

	Session createBeing(String authToken, String username, String beingClass, String beingName, String worldName, Integer placeCode) throws ClientException;
	
	Session destroyBeing(String authToken, String username, Long beingCode) throws ClientException;
	
	String createSession(String username, String password, String clientType, String ipAddress) throws ClientException;
	
	Session getSession(String authToken, String username) throws ClientException;

	Action insertCommand(String authToken, String verb, Long actorCode,
			Optional<String> mediatorCode, Optional<String> mediatorType,
			String targetCode, String targetType) throws ClientException;
	
	Being getBeing(String authToken, Long beingCode) throws ClientException;
	
	Item getItem(String authToken, Long itemId) throws ClientException;

	Place getPlace(String authToken, Integer placeId) throws ClientException;
	
	List<Message> getMessages(String authToken);
	
	List<BeingClass> getBeingClasses(String authToken);
	
}
