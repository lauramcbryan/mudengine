package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;

import com.jpinfo.mudengine.world.model.pk.PlaceExitPK;

import lombok.Data;

@Entity
@Table(name="MUD_PLACE_EXIT")
@Data
public class MudPlaceExit {

	@EmbeddedId
	private PlaceExitPK pk;

	@Column(length = 30)
	private String name;
	
	@Column
	@ColumnDefault(value = "true")	
	private boolean opened;
	
	@Column
	@ColumnDefault(value = "true")	
	private boolean visible;
	
	@Column
	@ColumnDefault(value = "false")
	private boolean locked;
	
	@Column	
	@ColumnDefault(value = "false")	
	private boolean lockable;

	@Column(nullable = false)
	private Integer targetPlaceCode;	
}
