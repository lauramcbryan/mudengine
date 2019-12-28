package com.jpinfo.mudengine.player.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.service.PlayerService;
import com.jpinfo.mudengine.player.service.PlayerServiceImpl;

@RestController
public class PlayerController implements PlayerService {
	
	@Autowired
	private PlayerServiceImpl service;
	
	
	@Override
	public Player getPlayerDetails(@PathVariable String username) {
		
		return service.getPlayerDetails(username);
	}

	@Override
	public ResponseEntity<Player> registerPlayer(@PathVariable String username, @RequestParam String email, @RequestParam String locale) {
		
		Player response = service.registerPlayer(username, email, locale);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Player> updatePlayerDetails(@PathVariable String username, @RequestBody Player playerData) {
		
		Player response = service.updatePlayerDetails(username, playerData);
			
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	@Override
	public void setPlayerPassword(@PathVariable String username, @RequestParam String activationCode, @RequestParam String newPassword) {
		
		service.setPlayerPassword(username, activationCode, newPassword);
	}

	@Override
	public Session getActiveSession(@PathVariable String username) {
		
		return service.getActiveSession(username);
	}

	@Override
	public ResponseEntity<Session> createSession(@PathVariable String username, @RequestParam String password, @RequestParam String clientType, @RequestParam String ipAddress) {
		
		Session response = service.createSession(username, password, clientType, ipAddress);
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<Session> setActiveBeing(@PathVariable String username, @PathVariable Long beingCode) {

		return new ResponseEntity<>(service.setActiveBeing(username, beingCode), HttpStatus.ACCEPTED);
	}
		
	
	@Override
	public ResponseEntity<Player> createBeing(@PathVariable String username, @RequestParam String beingClass, @RequestParam String beingName,
			@RequestParam String worldName, @RequestParam Integer placeCode) {
		
		Player response = service.createBeing(username, beingClass, beingName, worldName, placeCode);
		
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<Player> destroyBeing(@PathVariable String username, @PathVariable Long beingCode) {
		
		Player response = service.destroyBeing(username, beingCode);
		
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}
}
