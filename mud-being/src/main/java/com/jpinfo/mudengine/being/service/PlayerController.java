package com.jpinfo.mudengine.being.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.being.model.Player;
import com.jpinfo.mudengine.being.repository.PlayerRepository;

@RestController
@RequestMapping("/player")
public class PlayerController {
	
	@Autowired
	private PlayerRepository repository;

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	public Player getPlayer(@PathVariable Integer id) {
		
		Player found = repository.findOne(id);
		
		return found;
		
	}
}
