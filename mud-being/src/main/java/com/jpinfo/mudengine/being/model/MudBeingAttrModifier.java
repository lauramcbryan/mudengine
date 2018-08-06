package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the mud_being_attr database table.
 * 
 */
@Entity
@Table(name="mud_being_attr_modifier")
@Data
@EqualsAndHashCode(of="id")
public class MudBeingAttrModifier implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingAttrModifierPK id;

	@Column(name="attr_offset")
	private float offset;

	@Column(name="end_turn")
	private Integer endTurn;
}