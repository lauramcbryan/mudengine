package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.BeingClassAttrPK;

import lombok.Data;


/**
 * The persistent class for the mud_being_class_attr database table.
 * 
 */
@Entity
@Table(name="mud_being_class_attr")
@Data
public class MudBeingClassAttr implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BeingClassAttrPK id;

	private Integer value;
}