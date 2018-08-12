package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.MudBeingAttrPK;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the mud_being_attr database table.
 * 
 */
@Entity
@Table(name="mud_being_attr")
@Data
@EqualsAndHashCode(of= {"id"})
public class MudBeingAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingAttrPK id;

	private Long value;
	
	@Transient
	public String getCode() {
		return id.getCode();
	}
	
}