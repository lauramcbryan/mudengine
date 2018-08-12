package com.jpinfo.mudengine.world.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;

import com.jpinfo.mudengine.world.model.pk.MudPlaceAttrPK;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="MUD_PLACE_ATTR")
@Data
@EqualsAndHashCode(of= {"id"})
public class MudPlaceAttr {

	@EmbeddedId
	private MudPlaceAttrPK id;
	
	@Column(name="VALUE", nullable = false)
	@ColumnDefault(value = "0")
	private Integer value;
	
	@Transient
	public String getCode() {
		return id.getCode();
	}
}
