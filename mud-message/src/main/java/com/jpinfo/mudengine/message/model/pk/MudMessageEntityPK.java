package com.jpinfo.mudengine.message.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class MudMessageEntityPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="MESSAGE_ID")
	private Long messageId;
	
	@Column(name="ENTITY_ID")
	private Long entityId;
	
	@Column(name="ENTITY_TYPE")
	private String entityType;
}
