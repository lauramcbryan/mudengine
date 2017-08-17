package com.jpinfo.mudengine.common.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

@RequestMapping("/player")
public interface PlayerService {

	@RequestMapping(method=RequestMethod.GET, value="/{username}")
	Player getPlayerDetails(@PathVariable("username") String username);

	@RequestMapping(method=RequestMethod.PUT, value="/{username}")
	ResponseEntity<Player> registerPlayer(@PathVariable("username") String username, @RequestParam("email") String email);

	@RequestMapping(method=RequestMethod.POST, value="/{username}")
	ResponseEntity<Player> updatePlayerDetails(@PathVariable("username") String username, @RequestBody Player playerData);
	
	@RequestMapping(method=RequestMethod.POST, value="/{username}/password")
	void setPlayerPassword(@PathVariable("username") String username, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{username}")
	void deletePlayer(@PathVariable("username") String username);
	
	
	@RequestMapping(method=RequestMethod.GET, value="/{username}/session")
	Session getActiveSession(@PathVariable("username") String username);
	
	@RequestMapping(method=RequestMethod.PUT, value="/{username}/session")
	ResponseEntity<Session> createSession(@PathVariable("username") String username, @RequestParam("password") String password);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{username}/session")
	void deleteActiveSession(@PathVariable("username") String username);
	

}