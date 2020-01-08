package com.jpinfo.mudengine.common.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

@RequestMapping("/player")
public interface PlayerService {

	@GetMapping
	Player getPlayerDetails();

	@PutMapping(value="/{username}")
	ResponseEntity<Player> registerPlayer(@PathVariable("username") String username, @RequestParam("email") String email, @RequestParam("locale") String locale);

	
	/**
	 * Change the password
	 * @param username
	 * @param oldPassword
	 * @param newPassword
	 */
	@PostMapping(value="/{username}/password")
	void setPlayerPassword(@PathVariable("username") String username, @RequestParam("activationCode") String activationCode, @RequestParam("newPassword") String newPassword);

	@PostMapping()
	ResponseEntity<Player> updatePlayerDetails(@RequestBody Player playerData);

	@PutMapping(value="/being")
	ResponseEntity<Player> createBeing( 
			@RequestParam("beingClass") String beingClass, @RequestParam("beingName") String beingName, 
			@RequestParam("worldName") String worldName, @RequestParam("placeCode") Integer placeCode);
	
	@DeleteMapping(value="/being/{beingCode}")
	ResponseEntity<Player> destroyBeing(@PathVariable("beingCode") Long beingCode);
	

	@PutMapping(value="/{username}/session")
	ResponseEntity<Session> createSession(@PathVariable("username") String username, @RequestParam("password") String password, 
			@RequestParam("clientType") String clientType, @RequestParam("ipAddress") String ipAddress);
	
	@GetMapping(value="/session")
	Session getActiveSession();

	@PostMapping(value="/session/being/{beingCode}")
	ResponseEntity<Session> setActiveBeing(@PathVariable("beingCode") Long beingCode);

}