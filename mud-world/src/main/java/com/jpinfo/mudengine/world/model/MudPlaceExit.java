package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;

import com.jpinfo.mudengine.world.model.pk.MudPlaceExitPK;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="MUD_PLACE_EXIT")
@Data
@EqualsAndHashCode(of="pk")
public class MudPlaceExit {

	@EmbeddedId
	private MudPlaceExitPK pk;

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

	@Column(name="TARGET_PLACE_CODE", nullable = false)
	private Integer targetPlaceCode;
	
	@Transient
	public String getDirection() {
		return pk.getDirection();
	}
}
