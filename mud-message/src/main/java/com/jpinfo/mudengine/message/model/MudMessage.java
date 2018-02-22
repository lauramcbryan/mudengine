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

@Entity
@Table(name="MUD_MESSAGE")
@SequenceGenerator(name="mud_message_seq", sequenceName="MUD_MESSAGE_SEQ", allocationSize=1)
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
	
	@OneToMany(mappedBy="id.messageId", cascade=CascadeType.ALL, orphanRemoval=true)
	@OrderBy("eval_order ASC")
	private Set<MudMessageParm> parms;
	
	
	public MudMessage() {
		this.parms = new HashSet<MudMessageParm>();
	}

	public Long getBeingCode() {
		return beingCode;
	}

	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Timestamp getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Timestamp insertDate) {
		this.insertDate = insertDate;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public Boolean getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(Boolean readFlag) {
		this.readFlag = readFlag;
	}

	public Set<MudMessageParm> getParms() {
		return parms;
	}

	public void setParms(Set<MudMessageParm> parms) {
		this.parms = parms;
	}

	public Long getSenderCode() {
		return senderCode;
	}

	public void setSenderCode(Long senderCode) {
		this.senderCode = senderCode;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	
}
