package com.jpinfo.mudengine.common.utils;

import lombok.Data;

@Data
public class NotificationMessage {
	
	public enum EnumEntity { PLACE, ITEM, BEING }
	
	public enum EnumNotificationEvent {
		PLACE_CLASS_CHANGE, PLACE_ATTR_CHANGE, PLACE_DESTROY,
		PLACE_EXIT_CREATE, PLACE_EXIT_DESTROY, 
		PLACE_EXIT_OPEN, PLACE_EXIT_CLOSE, PLACE_EXIT_LOCK, PLACE_EXIT_UNLOCK,
	
		ITEM_CHANGE_CLASS, ITEM_CHANGE_ATTR, ITEM_DESTROY,
		ITEM_TAKEN, ITEM_DROP,
		ITEM_INCREATE_QTTY, ITEM_DECREASE_QTTY
	}

	private EnumEntity entity;
	private Long entityId;
	private EnumNotificationEvent event;
	
	private String messageKey;
	private String[] args;
}
