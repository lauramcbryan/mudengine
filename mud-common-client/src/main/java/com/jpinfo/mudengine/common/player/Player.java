package com.jpinfo.mudengine.common.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * The persistent class for the mud_player database table.
 * 
 */
@Data
public class Player implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int STATUS_PENDING = 0;
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_INACTIVE = 2;
	public static final int STATUS_BLOCKED = 3;
	public static final int STATUS_BANNED = 4;

	private Long playerId;

	private String username;
	
	private String email;
	
	private String locale;
	
	private Integer status;
	
	private String strStatus;
	
	private List<PlayerBeing> beingList;

	public Player() {
		this.beingList = new ArrayList<PlayerBeing>();
	}
	
	public Player(Map<String, Object> map) {
		
		this();

		this.playerId = new Long(String.valueOf(map.get("playerId")));
		this.username = (String)map.get("username");
		this.email = (String)map.get("email");
		this.locale = (String)map.get("locale");
		this.status = (Integer)map.get("status");
	}
}