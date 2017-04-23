package com.jpinfo.mudengine.being.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.being.model.MudPlayer;
import com.jpinfo.mudengine.being.repository.PlayerRepository;
import com.jpinfo.mudengine.being.utils.PlayerHelper;
import com.jpinfo.mudengine.common.being.Player;

@RestController
@RequestMapping("/player")
public class PlayerController {
	
	@Autowired
	private PlayerRepository repository;

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	public Player getPlayer(@PathVariable Long id) {
		
		Player result = null;
		
		MudPlayer found = repository.findOne(id);
		
		if (found!=null)
			result = PlayerHelper.buildPlayer(found);
		
		return result;
		
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/login/{login}")
	public Player getPlayer(@PathVariable String login) {
		
		Player result = null;
		MudPlayer dbPlayer = repository.findByLogin(login);
		
		if (dbPlayer!=null)
			result = PlayerHelper.buildPlayer(dbPlayer);
		
		return result;
	}
}
