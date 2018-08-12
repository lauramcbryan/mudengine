package com.jpinfo.mudengine.world.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Embeddable
@Data
@EqualsAndHashCode
public class MudPlaceClassAttrPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="CLASS_CODE", length = 20)
	private String classCode;

	@Column(name="CODE", length = 5)
	private String code;
}
