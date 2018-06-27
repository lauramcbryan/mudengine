package com.jpinfo.mudengine.message.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Embeddable
@Data
@EqualsAndHashCode
public class MudMessageParmPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="MESSAGE_ID")
	private Long messageId;
	
	@Column(name="EVAL_ORDER")
	private Integer evalOrder;	
}