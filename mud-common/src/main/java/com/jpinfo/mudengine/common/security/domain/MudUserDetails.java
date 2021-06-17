package com.jpinfo.mudengine.common.security.domain;

import java.io.Serializable;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MudUserDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private Session sessionData;
	
	private Player playerData;
}
