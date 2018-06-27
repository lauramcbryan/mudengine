package com.jpinfo.mudengine.item.model.pk;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The primary key class for the mud_item_attr database table.
 * 
 */
@Embeddable
@Data
@EqualsAndHashCode
public class MudItemAttrPK implements Serializable {
	
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="item_code", insertable=false, updatable=false)
	private Long itemCode;

	@Column(name="attr_code")
	private String attrCode;
}