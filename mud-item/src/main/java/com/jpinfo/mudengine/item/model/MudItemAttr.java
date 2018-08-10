package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.item.model.pk.MudItemAttrPK;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the mud_item_attr database table.
 * 
 */
@Entity
@Table(name="mud_item_attr")
@Data
@EqualsAndHashCode(of= {"id"})
public class MudItemAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudItemAttrPK id;

	private Integer value;
	
	@Transient
	public String getCode() {
		return id.getCode();
	}
}