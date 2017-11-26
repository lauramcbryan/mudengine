package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;

import com.jpinfo.mudengine.world.model.pk.PlaceExitPK;

@Entity
@Table(name="MUD_PLACE_EXIT")
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
	

	public PlaceExitPK getPk() {
		return pk;
	}

	public void setPk(PlaceExitPK pk) {
		this.pk = pk;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Integer getTargetPlaceCode() {
		return targetPlaceCode;
	}

	public void setTargetPlaceCode(Integer targetPlaceCode) {
		this.targetPlaceCode = targetPlaceCode;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isLockable() {
		return lockable;
	}

	public void setLockable(boolean lockable) {
		this.lockable = lockable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
