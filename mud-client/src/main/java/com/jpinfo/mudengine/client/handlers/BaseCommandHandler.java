package com.jpinfo.mudengine.client.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.api.MudengineApi;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.interfaces.CommandHandler;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandParamState;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.action.CommandParam;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.player.PlayerBeing;

@Component
public abstract class BaseCommandHandler implements CommandHandler {
	
	private static final Logger log = LoggerFactory.getLogger(BaseCommandHandler.class);

	
	@Autowired
	protected MudengineApi api;

	
	private CommandParamState initializeBeingParameter(ClientConnection client, CommandParam paramTemplate) {
		
		CommandParamState newParam = new CommandParamState(paramTemplate);

		// Populate with all being in this place
		client.getCurPlace().ifPresent(e -> {
			
			try {
				newParam.setDynamicDomainValues(
						api.getBeingsFromPlace(client.getAuthToken(), "aforgotten", e.getCode()).stream()
						.collect(Collectors.toMap(
								Being::getName, Being::getCode))
						);
				
			} catch(ClientException f) {
				log.error("Error while initializing commandState", f);
			}
		});
		
		
		return newParam;
	}
	
	private CommandParamState initializeBeingClassesParameter(ClientConnection client, CommandParam paramTemplate) throws ClientException {

		CommandParamState newParam = new CommandParamState(paramTemplate);
		
		// The being class is not provided, show the being class list
		List<BeingClass> beingClassList = api.getBeingClasses(client.getAuthToken());
		
		newParam.setDynamicDomainValues(
				beingClassList.stream()
					.collect(Collectors.toMap(
							BeingClass::getName, BeingClass::getCode)
							)
				);
		
		return newParam;
	}
	
	private CommandParamState initializeDirectionParameter(ClientConnection client, CommandParam paramTemplate) {

		CommandParamState newParam = new CommandParamState(paramTemplate);
		
		client.getCurPlace().ifPresent(e ->
		newParam.setDynamicDomainValues(
				e.getExits().keySet().stream()
				.collect(Collectors.toMap(
						String::toString, String::toString))
				)
		);
		
		
		return newParam;

	}
	
	private CommandParamState initializeItemParameter(ClientConnection client, CommandParam paramTemplate) {

		CommandParamState newParam = new CommandParamState(paramTemplate);
		
		// Populate with all being in this place
		client.getCurPlace().ifPresent(e -> {
			
			try {
				
				List<Item> itemList = new ArrayList<>();
				
				itemList.addAll(api.getItemsFromPlace(client.getAuthToken(), "aforgotten", e.getCode()));
				itemList.addAll(api.getItemsFromBeing(client.getAuthToken(), client.getActiveBeingCode().get()));
				
				newParam.setDynamicDomainValues(
						itemList.stream()
						.collect(Collectors.toMap(
								Item::getClassCode, Item::getCode))
						);
				
			} catch(ClientException f) {
				log.error("Error while initializing commandState", f);
			}
		});
		
		return newParam;
		
	}
	
	private CommandParamState initializePlayerBeingsParameter(ClientConnection client, CommandParam paramTemplate) {
		
		CommandParamState newParam = new CommandParamState(paramTemplate);
		
		// If there's an active player
		client.getPlayerData().ifPresent(e ->
		
			// Populate the domainValues for the current parameter
			// with the list of beings available for that player
			newParam.setDynamicDomainValues(
					
				// Converting the player being list to Map<String, Object>
				// With that the user will be able to type the beingName
				// and the code will be associated
				e.getBeingList().stream()
					.collect(Collectors.toMap(
							PlayerBeing::getBeingName, 
							PlayerBeing::getBeingCode)
							)
					)
			);

		
		
		return newParam;
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.jpinfo.mudengine.client.service.CommandHandler#initializeCommand(com.jpinfo.mudengine.client.model.ClientConnection, com.jpinfo.mudengine.common.action.Command)
	 */
	@Override
	public CommandState initializeCommand(ClientConnection client, Command command) {
		
		// Creates the CommandParamState list from all the static params provided
		List<CommandParamState> paramList = new ArrayList<>();
		
		if (command.getParameters()!=null)
			paramList =
				command.getParameters().stream()
					.map(d -> {
						
						CommandParamState newParam;
						
						try {
						
							switch(d.getType()) {
							case BEING:
								newParam = initializeBeingParameter(client, d);
								break;
							case BEING_CLASSES:
								newParam = initializeBeingClassesParameter(client, d);
								break;
							case DIRECTION:
								newParam = initializeDirectionParameter(client, d);
								
								break;
							case ITEM:
								newParam = initializeItemParameter(client, d);
								
								break;
							case PLAYER_BEINGS:
								newParam = initializePlayerBeingsParameter(client, d);
								break;
								
							default:
								newParam = new CommandParamState(d);
								break;
							}
						} catch(ClientException e) {
							log.error("Error initializing a CommandState", e);
							newParam = new CommandParamState(d);
						}
						
						
						return newParam;
					})
				.collect(Collectors.toList());
		
		return new CommandState(command, paramList);
	}
}