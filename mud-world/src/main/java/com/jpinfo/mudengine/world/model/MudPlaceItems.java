package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import com.jpinfo.mudengine.world.model.pk.PlaceItemsPK;

@Entity
@Table(name="MUD_PLACE_ITEMS")
public class MudPlaceItems {
	
	@EmbeddedId
	private PlaceItemsPK pk;
	
	@Column
	private String name;
	
	@Column
	private Integer qtty;

	public PlaceItemsPK getPk() {
		return pk;
	}

	public void setPk(PlaceItemsPK pk) {
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
