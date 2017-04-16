package com.jpinfo.mudengine.common.place;


public class PlaceExits {

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getTargetPlaceCode() {
		return targetPlaceCode;
	}

	public void setTargetPlaceCode(Integer targetPlaceCode) {
		this.targetPlaceCode = targetPlaceCode;
	}

	private String direction;
	
	private String name;
	
	private boolean opened;
	
	private boolean visible;
	
	private boolean locked;
	
	private boolean lockable;

	private Integer targetPlaceCode;
}
