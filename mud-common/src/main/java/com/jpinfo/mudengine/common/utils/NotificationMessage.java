package com.jpinfo.mudengine.common.utils;

import java.io.Serializable;

import lombok.Data;

@Data
public class NotificationMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	public enum EnumEntity { PLACE, ITEM, BEING }
	
	public enum EnumNotificationEvent {
		PLACE_CLASS_CHANGE, PLACE_ATTR_CHANGE, PLACE_DESTROY,
		PLACE_EXIT_CREATE, PLACE_EXIT_DESTROY, 
		PLACE_EXIT_OPEN, PLACE_EXIT_CLOSE, PLACE_EXIT_LOCK, PLACE_EXIT_UNLOCK,
	
		ITEM_CLASS_CHANGE, ITEM_ATTR_CHANGE, ITEM_DESTROY,
		ITEM_TAKEN, ITEM_DROP,
		ITEM_QTTY_INCREASE, ITEM_QTTY_DECREASE,
		
		BEING_CLASS_CHANGE, BEING_WORLD_CHANGE, BEING_DESTROY,
		BEING_SKILL_CHANGE, BEING_ATTR_CHANGE, 
	}

	private EnumEntity entity;
	private Long entityId;
	private EnumNotificationEvent event;
	private String worldName;
	
	private EnumEntity targetEntity;
	private Long targetEntityId;
	
	private String messageKey;
	private String[] args;
}
