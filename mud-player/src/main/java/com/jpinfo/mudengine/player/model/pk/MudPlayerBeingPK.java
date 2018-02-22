package com.jpinfo.mudengine.player.model.pk;

import javax.persistence.Embeddable;

@Embeddable
public class MudPlayerBeingPK implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Long playerId;
	
	private Long beingCode;

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public Long getBeingCode() {
		return beingCode;
	}

	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beingCode == null) ? 0 : beingCode.hashCode());
		result = prime * result + ((playerId == null) ? 0 : playerId.hashCode());
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
		MudPlayerBeingPK other = (MudPlayerBeingPK) obj;
		if (beingCode == null) {
			if (other.beingCode != null)
				return false;
		} else if (!beingCode.equals(other.beingCode))
			return false;
		if (playerId == null) {
			if (other.playerId != null)
				return false;
		} else if (!playerId.equals(other.playerId))
			return false;
		return true;
	}
}
