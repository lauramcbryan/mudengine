package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.item.model.pk.MudItemClassAttrPK;

import lombok.Data;


/**
 * The persistent class for the mud_item_class_attr database table.
 * 
 */
@Entity
@Table(name="mud_item_class_attr")
@Data
public class MudItemClassAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudItemClassAttrPK id;

	private Integer value;
	
	@Transient
	public String getCode() {
		return id.getCode();
	}
}