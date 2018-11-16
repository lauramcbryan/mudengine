package com.jpinfo.mudengine.message.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MudMessageLocalePK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name="MESSAGE_KEY")
	private String messageKey;
	
	@Column(name="LOCALE")
	private String locale;
}