package com.jpinfo.mudengine.being.utils;

import java.util.Date;

public class BeingHelper {
	
	public static final String BEING_DESTROY_YOURS_MSG = "being.destroy.yours";
	public static final String BEING_DESTROY_ANOTHER_MSG = "being.destroy.another";

	public static final String BEING_ATTRMOD_INCREASE_MSG = "being.attrmod.increase";
	public static final String BEING_ATTRMOD_DECREASE_MSG = "being.attrmod.decrease";
	public static final String BEING_SKILLMOD_INCREASE_MSG = "being.skillmod.increase";
	public static final String BEING_SKILLMOD_DECREASE_MSG = "being.skillmod.decrease";
	

	public static final String BEING_TAKE_YOURS_MSG = "being.take.yours";
	public static final String BEING_TAKE_ANOTHER_MSG = "being.take.another";
	
	public static final String BEING_DROP_YOURS_MSG = "being.drop.yours";
	public static final String BEING_DROP_ANOTHER_MSG = "being.drop.another";
	
	
	public static final String BEING_EQUIP_MSG = "being.equip";
	public static final String BEING_UNEQUIP_MSG = "being.unequip";
	
	
	public static final int CREATE_DEFAULT_QUANTITY = 1;
	
	public static final String BEING_HP_ATTR = "HP";
	public static final String BEING_MAX_HP_ATTR = "MAXHP";
	
	private static final long ONE_WEEK = (7 * 24 * 60 * 1000);
	
	private BeingHelper() {}
	
	public static Date calculateOneWeekAgo() {
		
		return new Date(System.currentTimeMillis() - BeingHelper.ONE_WEEK);
	}
	
}
