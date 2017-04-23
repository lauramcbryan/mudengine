package com.jpinfo.mudengine.action.utils;

import org.springframework.web.client.RestTemplate;



import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.common.action.ActionState;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionSimpleState;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.placeClass.PlaceClass;

public class ActionHelper {
	
	public static ActionSimpleState buildSimpleState(MudAction a) {
		
		ActionSimpleState state = new ActionSimpleState();
		
		state.setActionId(a.getActionId());
		state.setStartTurn(a.getStartTurn());
		state.setEndTurn(a.getEndTurn());
		state.setCurState(a.getCurrState());
		
		return state;
		
	}

	public static ActionState buildAction(MudAction a) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		ActionState result = new ActionState();

		result.setActionId(a.getActionId());
		result.setActionCode(a.getActionCode());
		
		//Actor		
		if (a.getActorCode()!=null) {
			
			Being actor = restTemplate.getForObject(getBeingServiceUrl(), Being.class, a.getActorCode());
			result.setActor(actor);
		}
		
		// Mediator
		if (a.getMediatorCode()!=null) {
			
			Item item = restTemplate.getForObject(getItemServiceUrl(), Item.class, a.getMediatorCode());
			result.setMediator(item);
		}
		
		// Place
		if (a.getPlaceCode()!=null) {
			
			Place place = restTemplate.getForObject(getPlaceServiceUrl(), Place.class, a.getPlaceCode());
			result.setPlace(place);
		}
		
		if (a.getTargetCode()!=null) {
			
			ActionTarget target = null;
			
			switch(a.getTargetType()) {
			case "ITEM":
				target = restTemplate.getForObject(getItemServiceUrl(), Item.class, a.getTargetCode());
				break;
			case "PLACE":
				target = restTemplate.getForObject(getPlaceServiceUrl(), Place.class, a.getTargetCode());
				break;
			case "BEING":
				target = restTemplate.getForObject(getBeingServiceUrl(), Being.class, a.getTargetCode());
				break;
			case "PLACE_CLASS":
				target = restTemplate.getForObject(getPlaceClassServiceUrl(), PlaceClass.class, a.getTargetCode());
				break;
			}
			
			result.setTarget(target);
		}
		
		return result;
	}
	
	public static MudAction buildMudAction(Action requestAction) {
		
		MudAction mudAction = new MudAction();
		
		mudAction.setActorCode(requestAction.getActorCode());
		mudAction.setIssuerCode(requestAction.getIssuerCode());
		
		mudAction.setActionCode(requestAction.getActionCode());
		mudAction.setMediatorCode(requestAction.getMediatorCode());
		mudAction.setPlaceCode(requestAction.getPlaceCode());
		mudAction.setTargetCode(requestAction.getTargetCode());
		mudAction.setTargetType(requestAction.getTargetType());
		mudAction.setWorldName(requestAction.getWorldName());
		
		return mudAction;
	}
	
	
	private static String getPlaceServiceUrl() {
		return "http://localhost:8080/place/{id}";
	}

	private static String getPlaceClassServiceUrl() {
		return "http://localhost:8080/place/class/{id}";
	}
	
	private static String getItemServiceUrl() {
		return "http://localhost:8084/item/{id}";
	}

	private static String getBeingServiceUrl() {
		return "http://localhost:8088/being/{id}";
	}
	
}
