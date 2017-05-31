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
}
