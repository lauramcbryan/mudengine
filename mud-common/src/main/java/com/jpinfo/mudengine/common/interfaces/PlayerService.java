package com.jpinfo.mudengine.common.interfaces;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jpinfo.mudengine.common.being.Player;

@RequestMapping("/player")
public interface PlayerService {

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	Player getPlayer(@PathVariable("id") Long id);

	@RequestMapping(method=RequestMethod.GET, value="/login/{login}")
	Player getPlayerByLogin(@PathVariable("login") String login);

}