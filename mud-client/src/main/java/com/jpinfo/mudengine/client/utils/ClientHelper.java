package com.jpinfo.mudengine.client.utils;

import java.util.Scanner;

import com.jpinfo.mudengine.common.message.Message;


public class ClientHelper {
	
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
	
	public static String formatMessage(Message m) {
		
		String response = null;
		
		if (m.getSenderCode()!=null) {
			response = String.format("[%s] %s: %s", m.getMessageDate(), m.getSenderName(), m.getMessage());
		} else {
			response = String.format("[%s]: %s", m.getMessageDate(), m.getMessage());
		}
		
		return response;
	}
}
