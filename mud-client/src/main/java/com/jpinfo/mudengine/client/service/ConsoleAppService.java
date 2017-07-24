package com.jpinfo.mudengine.client.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.client.utils.ConsoleHelper;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.Player;
import com.jpinfo.mudengine.common.client.BeingServiceClient;
import com.jpinfo.mudengine.common.client.PlaceServiceClient;
import com.jpinfo.mudengine.common.client.PlayerServiceClient;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;

@Service
public class ConsoleAppService implements Runnable {
		
	private Place currentPlace;
	
	private Player currentPlayer;
	
	private Being currentBeing;
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private PlayerServiceClient playerService;
	
	@Autowired
	private PlaceServiceClient placeService;
	
	
	public void login(String login, String password) {
		
		currentPlayer = playerService.getPlayerByLogin(login);
	}
	
	public void logoff() {
		currentPlayer = null;
		currentBeing = null;
		currentPlace = null;
	}
	
	public void selectBeing() {
		
		int choose = 0;
		int k=0;
		
		Iterable<Being> beingList = beingService.getAllFromPlayer(currentPlayer.getPlayerId());
		
		/*
		 * 
		 * beingList.forEach(arg0);
		 * 
		for(;k<beingList.getBody().length;k++) {
			
			Being curBeing = beingList.getBody()[k];

			System.out.println("\t" + (k+1) + ": " + curBeing.getName() + "[class: " + curBeing.getBeingClass() + "]");
		}
		*/
		
		System.out.print("\n\nPick one[1-" + k + "]: ");
		
		choose = ConsoleHelper.readIntInput();
		
		//currentBeing = beingList.getBody()[choose-1];
	}
	
	public void showBeingStatus() {
		
		System.out.println("Stats");
		
		if (currentBeing!=null) {
			
			StringBuilder stats = new StringBuilder();
			
			for(String curAttr: currentBeing.getAttrs().keySet()) {
				stats.append("\t").append(curAttr).append(": ").append(currentBeing.getAttrs().get(curAttr));
				
				Float modifier = ConsoleHelper.calculateAttrModifiers(currentBeing, curAttr);
				
				if (modifier!=0) {
					stats.append(" [").append(modifier).append("]");
				}
			}
			
			System.out.println(stats);
			
		}
		
	}
	
	public void showPlace() {
		
		System.out.println("Place");
		
		if (currentBeing!=null) { 
			if ((currentPlace==null) || (!currentPlace.getPlaceCode().equals(currentBeing.getCurPlaceCode()))) {
				
				currentPlace = placeService.getPlace(currentBeing.getCurPlaceCode());
			}
		
			System.out.println("Items:");

			/*
			if (currentPlace.getItems().isEmpty()) {
				System.out.println("\tNone");
			} else {
				for(PlaceItems curItem: currentPlace.getItems()) {
					System.out.println("\t" + curItem.getName() + (curItem.getQtty() > 1 ? "[" + curItem.getQtty() + "]":""));
				}
			}
			*/
		
			System.out.println("Exits:");
			
			for (String curDirection: currentPlace.getExits().keySet()) {
				
				PlaceExit curExit = currentPlace.getExits().get(curDirection);
				
				if (curExit.isVisible()) {
					System.out.println("\t" + curDirection + ": " + curExit.getName());
				}
			}
		
			System.out.println("Beings:");
			
			/*
			if (currentPlace.getBeings().isEmpty()) {
				System.out.println("\tNone");
			} else {
				for(PlaceBeings curBeing: currentPlace.getBeings()) {
					
					System.out.println("\t" + curBeing.getName() + (curBeing.getQtty() > 1 ? "[" + curBeing.getQtty() + "]":""));
				}
			}
			*/
		} else {
			System.out.println("Unknown");
		}
	}
	
	public void run() {
		
		int option = -1;
		
		while (option!=0) {
		
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			
			System.out.println("Player: " + (currentPlayer!=null ? currentPlayer.getName(): "None"));
			System.out.println("Character: " + (currentBeing!=null? currentBeing.getName() : "None"));
			System.out.println("World: " + (currentBeing!=null ? currentBeing.getCurWorld() : "Unknown"));
			
			System.out.println("================================================================================");
			showPlace();
			
			System.out.println("================================================================================");
			showBeingStatus();
			
			System.out.println("================================================================================");
			
			// Options
			
			if (currentPlace!=null) {
				System.out.println("1 - Examine place");
				System.out.println("2 - Walk to direction");
				System.out.println("3 - Examine item");
			}
			
			if (currentPlayer!=null) {
				System.out.println("8 - Select char");
			}
			
			if (currentPlayer!=null) {
				System.out.println("9 - logoff");
			} else {
				System.out.println("9 - login");
			}
			
			System.out.println("0 - Exit");
			
			option = ConsoleHelper.readIntInput();
			
			switch(option) {
			case 8: {
				selectBeing();
				break;
			}
			case 9: {
				
				if (currentPlayer!=null) {
					logoff();
				} else {
					System.out.print("Type your login: ");
					String login = ConsoleHelper.readString();
					System.out.print("Type your password: ");
					String password = ConsoleHelper.readString();
					
					login(login, password);
				}
				break;
			}
			
			case 10:
				return;
			} // endswitch
		} // wend
		
		System.out.println("So long");
	}
}
