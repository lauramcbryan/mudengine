package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import com.jpinfo.mudengine.world.model.pk.PlaceBeingsPK;

@Entity
@Table(name="MUD_PLACE_BEINGS")
public class MudPlaceBeings {

	@EmbeddedId
	private PlaceBeingsPK pk;

	@Column
	private String name;
	
	@Column
	private Integer qtty;
	
	
	public PlaceBeingsPK getPk() {
		return pk;
	}

	public void setPk(PlaceBeingsPK pk) {
		this.pk = pk;
	}

	public Integer getQtty() {
		return qtty;
	}

	public void setQtty(Integer qtty) {
		this.qtty = qtty;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
