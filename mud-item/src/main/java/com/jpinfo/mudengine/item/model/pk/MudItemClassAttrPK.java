package com.jpinfo.mudengine.item.model.pk;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The primary key class for the mud_item_class_attr database table.
 * 
 */
@Embeddable
@Data
@EqualsAndHashCode
public class MudItemClassAttrPK implements Serializable {
	
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="item_class_code", insertable=false, updatable=false)
	private String itemClassCode;

	@Column(name="attr_code")
	private String attrCode;
}