package com.jpinfo.mudengine.client.utils;

import java.util.Scanner;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;

public class ConsoleHelper {
	
	private static Scanner console = new Scanner(System.in);
	
	public static int readIntInput() {
		
		int result = console.nextInt();
		
		if (console.hasNextLine()) {
			console.nextLine();
		}
		
		
		return result;
	}
	
	public static String readString() {
		
		String result = console.nextLine();
		
		return result;
	}
	
	public static Float calculateAttrModifiers(Being curBeing, String attr) {
		
		Float result = 0.0f;
		
		for(BeingAttrModifier curModifier: curBeing.getAttrModifiers()) {
			
			if (curModifier.getAttribute().equals(attr)) {
				result+=curModifier.getOffset();
			}
		}
		
		return result;
	}
	
	public static String getPlaceServiceUrl() {
		return "http://localhost:8080/place/{id}";
	}

	public static String getPlaceClassServiceUrl() {
		return "http://localhost:8080/place/class/{id}";
	}
	
	public static String getItemServiceUrl() {
		return "http://localhost:8084/item/{id}";
	}

	public static String getBeingServiceUrl() {
		return "http://localhost:8088/being/{id}";
	}
	
	public static String getBeingByPlayerServiceUrl() {
		return "http://localhost:8088/being/player/{playerId}";
	}
	
	public static String getPlayerServiceUrl() {
		return "http://localhost:8088/player/{id}";
	}
	
	public static String getPlayerByLoginServiceUrl() {
		return "http://localhost:8088/player/login/{login}";
	}
	
}
