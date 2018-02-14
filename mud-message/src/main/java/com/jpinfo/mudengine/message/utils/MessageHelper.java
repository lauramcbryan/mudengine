package com.jpinfo.mudengine.message.utils;

public class MessageHelper {
	
	public static boolean isLocalizedKey(String messageKey) {

		return messageKey.startsWith("${");
	}
	
	public static String getLocalizedKey(String messageKey) {
		
		return messageKey.substring(2, messageKey.length()-1);
	}
}
