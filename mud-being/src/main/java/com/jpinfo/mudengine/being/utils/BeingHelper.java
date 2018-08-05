package com.jpinfo.mudengine.being.utils;

import java.util.Date;

public class BeingHelper {
	
	private static final long ONE_WEEK = (7 * 24 * 60 * 1000);

	private BeingHelper() {}
	
	public static Date calculateOneWeekAgo() {
		
		return new Date(System.currentTimeMillis() - BeingHelper.ONE_WEEK);
	}
	
}
