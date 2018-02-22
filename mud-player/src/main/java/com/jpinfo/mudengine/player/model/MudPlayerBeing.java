package com.jpinfo.mudengine.player.model;

import java.util.Date;

import javax.persistence.*;

import com.jpinfo.mudengine.player.model.pk.MudPlayerBeingPK;

@Entity
@Table(name="MUD_PLAYER_BEING")
public class MudPlayerBeing {

	@EmbeddedId
	private MudPlayerBeingPK id;
	
	private String beingName;
	
	private String beingClass;
	
	private Date lastPlayed;

	public MudPlayerBeingPK getId() {
		return id;
	}

	public void setId(MudPlayerBeingPK id) {
		this.id = id;
	}

	public String getBeingName() {
		return beingName;
	}

	public void setBeingName(String beingName) {
		this.beingName = beingName;
	}

	public String getBeingClass() {
		return beingClass;
	}

	public void setBeingClass(String beingClass) {
		this.beingClass = beingClass;
	}

	public Date getLastPlayed() {
		return lastPlayed;
	}

	public void setLastPlayed(Date lastPlayed) {
		this.lastPlayed = lastPlayed;
	}	
}
