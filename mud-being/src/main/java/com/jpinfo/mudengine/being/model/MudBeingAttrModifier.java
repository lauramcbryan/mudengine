package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;


/**
 * The persistent class for the mud_being_attr database table.
 * 
 */
@Entity
@Table(name="mud_being_attr_modifier")
public class MudBeingAttrModifier implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingAttrModifierPK id;

	@Column(name="attr_offset")
	private float attrOffset;

	@Column(name="end_turn")
	private Integer endTurn;

	public MudBeingAttrModifier() {
	}

	public Integer getEndTurn() {
		return endTurn;
	}
	public void setEndTurn(Integer endTurn) {
		this.endTurn = endTurn;
	}

	public MudBeingAttrModifierPK getId() {
		return this.id;
	}

	public void setId(MudBeingAttrModifierPK id) {
		this.id = id;
	}

	public float getOffset() {
		return this.attrOffset;
	}

	public void setOffset(float attrOffset) {
		this.attrOffset = attrOffset;
	}
}