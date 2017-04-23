package com.jpinfo.mudengine.action.model;

import javax.persistence.*;

@Entity
@Table(name="MUD_ACTION")
@SequenceGenerator(name="MUD_ACTION_SEQ", sequenceName="MUD_ACTION_SEQ", allocationSize=1)
public class MudAction {
	
	@Id
	@Column(name="ACTION_UID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MUD_ACTION_SEQ")
	private Long actionId;
	
	@Column(name="ISSUER_CODE")
	private Integer issuerCode;
	
	@Column(name="ACTOR_CODE")
	private Integer actorCode;
	

	@Column(name="ACTION_CODE")
	private String actionCode;
	
	@Column(name="WORLD_NAME")
	private String worldName;
	
	@Column(name="MEDIATOR_CODE")
	private Integer mediatorCode;
	
	@Column(name="PLACE_CODE")
	private Integer placeCode;
	
	@Column(name="TARGET_CODE")
	private String targetCode;

	@Column(name="TARGET_TYPE")
	private String targetType;  // {BEING, ITEM, PLACE, PLACE_CLASS}
	
	public MudAction() {
		
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public Integer getMediatorCode() {
		return mediatorCode;
	}

	public void setMediatorCode(Integer mediatorCode) {
		this.mediatorCode = mediatorCode;
	}

	public Integer getPlaceCode() {
		return placeCode;
	}

	public void setPlaceCode(Integer placeCode) {
		this.placeCode = placeCode;
	}

	public String getTargetCode() {
		return targetCode;
	}

	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public Integer getIssuerCode() {
		return issuerCode;
	}

	public void setIssuerCode(Integer issuerCode) {
		this.issuerCode = issuerCode;
	}

	public Integer getActorCode() {
		return actorCode;
	}

	public void setActorCode(Integer actorCode) {
		this.actorCode = actorCode;
	}
	
	

}
