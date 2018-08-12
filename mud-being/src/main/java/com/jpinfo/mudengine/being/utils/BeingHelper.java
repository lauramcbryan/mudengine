package com.jpinfo.mudengine.being.utils;

import java.util.Date;

public class BeingHelper {

	public static final int CREATE_DEFAULT_QUANTITY = 1;
	
	public static final String BEING_HP_ATTR = "HP";
	public static final String BEING_MAX_HP_ATTR = "MAXHP";
	
	private static final long ONE_WEEK = (7 * 24 * 60 * 1000);
	
	private BeingHelper() {}
	
	public static Date calculateOneWeekAgo() {
		
		return new Date(System.currentTimeMillis() - BeingHelper.ONE_WEEK);
	}
	
}
