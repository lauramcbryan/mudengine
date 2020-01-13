package com.jpinfo.mudengine.player.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

import com.jpinfo.mudengine.player.model.pk.MudPlayerBeingPK;

import lombok.Data;

@Entity
@Table(name="MUD_PLAYER_BEING")
@Data
public class MudPlayerBeing implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudPlayerBeingPK id;
	
	private String beingName;
	
	private String beingClass;
	
	private LocalDateTime lastPlayed;
}
