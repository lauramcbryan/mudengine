package com.jpinfo.mudengine.action.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="MUD_ACTION_TURN")
@SequenceGenerator(name="MUD_ACTION_TURN_SEQ", sequenceName="MUD_ACTION_TURN_SEQ", allocationSize=1)
@Data
public class MudActionTurn {

	@Id
	@Column(name="NRO_TURN")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MUD_ACTION_TURN_SEQ")
	private Long nroTurn;
	
	@Column(name="STARTED_AT")
	private LocalDate startedAt;
	
	@Column(name="FINISHED_AT")
	private LocalDate finishedAt;
}
