package com.jpinfo.mudengine.being.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.being.model.MudPlayer;
import com.jpinfo.mudengine.being.repository.PlayerRepository;
import com.jpinfo.mudengine.being.utils.PlayerHelper;
import com.jpinfo.mudengine.common.being.Player;
import com.jpinfo.mudengine.common.interfaces.PlayerService;

@RestController
public class PlayerController implements PlayerService {
	
	@Autowired
	private PlayerRepository repository;

	@Override
	public Player getPlayer(@PathVariable Long id) {
		
		Player result = null;
		
		MudPlayer found = repository.findOne(id);
		
		if (found!=null)
			result = PlayerHelper.buildPlayer(found);
		
		return result;
		
	}
	
	@Override
	public Player getPlayerByLogin(@PathVariable String login) {
		
		Player result = null;
		MudPlayer dbPlayer = repository.findByLogin(login);
		
		if (dbPlayer!=null)
			result = PlayerHelper.buildPlayer(dbPlayer);
		
		return result;
	}
}
