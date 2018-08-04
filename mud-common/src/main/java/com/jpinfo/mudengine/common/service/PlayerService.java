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

	/**
	 * Create a profile for that username
	 * @param username
	 * @param email
	 * @param locale
	 * @return
	 */
	@RequestMapping(method=RequestMethod.PUT, value="/{username}")
	ResponseEntity<Player> registerPlayer(@PathVariable("username") String username, @RequestParam("email") String email, @RequestParam("locale") String locale);

	/**
	 * Update profile data
	 * @param authToken
	 * @param username
	 * @param playerData
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST, value="/{username}")
	ResponseEntity<Player> updatePlayerDetails(@PathVariable("username") String username, @RequestBody Player playerData);
	
	/**
	 * Change the password
	 * @param username
	 * @param oldPassword
	 * @param newPassword
	 */
	@RequestMapping(method=RequestMethod.POST, value="/{username}/password")
	void setPlayerPassword(@PathVariable("username") String username, @RequestParam("activationCode") String activationCode, @RequestParam("newPassword") String newPassword);
	

	@RequestMapping(method=RequestMethod.GET, value="/{username}/session")
	Session getActiveSession(@PathVariable("username") String username);

	@RequestMapping(method=RequestMethod.POST, value="/{username}/session/being/{beingCode}")
	ResponseEntity<Session> setActiveBeing(@PathVariable("username") String username, @PathVariable("beingCode") Long beingCode);

	@RequestMapping(method=RequestMethod.PUT, value="/{username}/being")
	ResponseEntity<Player> createBeing(@PathVariable("username") String username, 
			@RequestParam("beingClass") String beingClass, @RequestParam("beingName") String beingName, 
			@RequestParam("worldName") String worldName, @RequestParam("placeCode") Integer placeCode);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{username}/being/{beingCode}")
	ResponseEntity<Player> destroyBeing(@PathVariable("username") String username, @PathVariable("beingCode") Long beingCode);
	
	@RequestMapping(method=RequestMethod.PUT, value="/{username}/session")
	ResponseEntity<Session> createSession(@PathVariable("username") String username, @RequestParam("password") String password, 
			@RequestParam("clientType") String clientType, @RequestParam("ipAddress") String ipAddress);
}