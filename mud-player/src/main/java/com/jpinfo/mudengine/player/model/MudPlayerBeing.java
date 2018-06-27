package com.jpinfo.mudengine.player.model;

import java.util.Date;

import javax.persistence.*;

import com.jpinfo.mudengine.player.model.pk.MudPlayerBeingPK;

import lombok.Data;

@Entity
@Table(name="MUD_PLAYER_BEING")
@Data
public class MudPlayerBeing {

	@EmbeddedId
	private MudPlayerBeingPK id;
	
	private String beingName;
	
	private String beingClass;
	
	private Date lastPlayed;
}
