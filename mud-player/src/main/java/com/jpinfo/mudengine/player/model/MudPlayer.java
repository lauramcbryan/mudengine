package com.jpinfo.mudengine.player.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="MUD_PLAYER")
@SequenceGenerator(name="mud_player_seq", sequenceName="mud_player_seq", allocationSize=1)
@Data
public class MudPlayer implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="mud_player_seq", strategy=GenerationType.SEQUENCE)
	@Column(name="player_id")
	private Long playerId;
	
	private String username;
	
	private String password;
	
	@Column(length=64)
	private String email;
	
	private String locale;
	
	private Date createDate;
	
	private Integer status;
	
	@OneToMany(mappedBy="id.playerId", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudPlayerBeing> beingList = new ArrayList<>();
}
