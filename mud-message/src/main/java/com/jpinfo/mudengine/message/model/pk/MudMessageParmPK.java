package com.jpinfo.mudengine.message.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MudMessageParmPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="MESSAGE_ID")
	private Long messageId;
	
	@Column(name="EVAL_ORDER")
	private Integer evalOrder;
	
	public MudMessageParmPK() {
		
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Integer getEvalOrder() {
		return evalOrder;
	}

	public void setEvalOrder(Integer evalOrder) {
		this.evalOrder = evalOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((evalOrder == null) ? 0 : evalOrder.hashCode());
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MudMessageParmPK other = (MudMessageParmPK) obj;
		if (evalOrder == null) {
			if (other.evalOrder != null)
				return false;
		} else if (!evalOrder.equals(other.evalOrder))
			return false;
		if (messageId == null) {
			if (other.messageId != null)
				return false;
		} else if (!messageId.equals(other.messageId))
			return false;
		return true;
	}
	
}