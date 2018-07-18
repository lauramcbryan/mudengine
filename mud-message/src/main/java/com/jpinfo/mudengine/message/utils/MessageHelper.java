package com.jpinfo.mudengine.message.utils;

public class MessageHelper {
	
	private MessageHelper() { }
	
	public static boolean isLocalizedKey(String messageKey) {

		return messageKey.startsWith("{str:");
	}
	
	public static String getLocalizedKey(String messageKey) {
		
		return messageKey.substring("{str:".length(), messageKey.length()-1);
	}
}
