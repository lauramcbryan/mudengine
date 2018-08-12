package com.jpinfo.mudengine.world.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Embeddable
@Data
@EqualsAndHashCode
public class MudPlaceExitPK implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer placeCode;
	
	@Column(length = 10)
	private String direction;
	
}
