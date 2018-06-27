package com.jpinfo.mudengine.player.model.pk;

import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Embeddable
@Data
@EqualsAndHashCode
public class MudPlayerBeingPK implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Long playerId;
	
	private Long beingCode;
}
