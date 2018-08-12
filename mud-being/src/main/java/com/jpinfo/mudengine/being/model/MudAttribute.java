package com.jpinfo.mudengine.being.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the mud_attribute database table.
 * 
 */
@Entity
@Table(name="mud_attribute")
@Data
@EqualsAndHashCode(of= {"code"})
public class MudAttribute implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	private String code;

	private String description;

	private String name;
}