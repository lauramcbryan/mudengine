package com.jpinfo.mudengine.player.util;

import java.util.Random;

public class PlayerHelper {
	
	private static final int FIRST_PWD_CHAR = 48;  // '0'
	private static final int LAST_PWD_CHAR = 122;  // 'z'
	
	private PlayerHelper() {}

	public static String generatePassword() {
		
		Random rand = new Random(System.currentTimeMillis());
		
		StringBuilder hash = new StringBuilder(8);
		
		for(int k=0;k<8;k++)
			hash.append((char)(FIRST_PWD_CHAR + rand.nextInt(LAST_PWD_CHAR - FIRST_PWD_CHAR)));
		
		return hash.toString();
	}
}
