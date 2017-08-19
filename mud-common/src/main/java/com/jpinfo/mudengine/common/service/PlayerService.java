package com.jpinfo.mudengine.common.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.PlayerSimpleData;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.TokenService;

@RequestMapping("/player")
public interface PlayerService {

	@RequestMapping(method=RequestMethod.GET, value="/{username}")
	Player getPlayerDetails(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("username") String username);

	@RequestMapping(method=RequestMethod.PUT, value="/{username}")
	ResponseEntity<Player> registerPlayer(@PathVariable("username") String username, @RequestParam("email") String email, @RequestParam("language") String language);

	@RequestMapping(method=RequestMethod.POST, value="/{username}")
	ResponseEntity<Player> updatePlayerDetails(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("username") String username, @RequestBody PlayerSimpleData playerData);
	
	@RequestMapping(method=RequestMethod.POST, value="/{username}/password")
	void setPlayerPassword(@PathVariable("username") String username, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{username}")
	void deletePlayer(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("username") String username);
	
	
	@RequestMapping(method=RequestMethod.GET, value="/{username}/session")
	Session getActiveSession(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("username") String username);
	
	@RequestMapping(method=RequestMethod.PUT, value="/{username}/session")
	ResponseEntity<Session> createSession(@PathVariable("username") String username, @RequestParam("password") String password);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{username}/session")
	void deleteActiveSession(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("username") String username);
	

}