package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The primary key class for the mud_being_attr database table.
 * 
 */
@Embeddable
public class MudBeingAttrPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_code", insertable=false, updatable=false)
	private Integer beingCode;
	
	@Column(name="attr_code")
	private String attrCode;
	

	public MudBeingAttrPK() {
	}
	public Integer getBeingCode() {
		return this.beingCode;
	}
	public void setBeingCode(Integer beingCode) {
		this.beingCode = beingCode;
	}
	public String getAttrCode() {
		return attrCode;
	}
	public void setAttrCode(String attrCode) {
		this.attrCode = attrCode;
	}
	
	

}