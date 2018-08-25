package com.jpinfo.mudengine.message.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="MUD_MESSAGE")
@SequenceGenerator(name="mud_message_seq", sequenceName="MUD_MESSAGE_SEQ", allocationSize=1)
@Data
public class MudMessage {

	@Id
	@GeneratedValue(generator="mud_message_seq", strategy=GenerationType.SEQUENCE)
	@Column(name="MESSAGE_ID")
	private Long messageId;

	@Column(name="BEING_CODE")
	private Long beingCode;

	@Column(name="SENDER_CODE")
	private Long senderCode;

	@Column(name="SENDER_NAME")
	private String senderName;

	@Column(name="INSERT_DATE")
	private Timestamp insertDate;

	@Column(name="MESSAGE_KEY")
	private String messageKey;
	
	@Column(name="READ_FLAG")
	private Boolean readFlag;
	
	@Column(name="PLAIN_FLAG")
	private Boolean plainFlag;
	
	@OneToMany(mappedBy="id.messageId", cascade=CascadeType.ALL, orphanRemoval=true)
	@OrderBy("eval_order ASC")
	private Set<MudMessageParm> parms;
	
	
	public MudMessage() {
		this.parms = new HashSet<>();
	}
}
