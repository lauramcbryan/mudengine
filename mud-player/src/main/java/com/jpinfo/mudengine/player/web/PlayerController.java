package com.jpinfo.mudengine.player.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.service.PlayerService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import org.springframework.http.HttpHeaders;
import com.jpinfo.mudengine.player.service.PlayerServiceImpl;
import com.jpinfo.mudengine.player.service.SessionServiceImpl;

@RestController
public class PlayerController implements PlayerService {
	
	@Autowired
	private PlayerServiceImpl service;
	
	@Autowired
	private SessionServiceImpl sessionService;
	
	
	@Override
	public Player getPlayerDetails() {
		
		return service.getPlayerDetails();
	}

	@Override
	public ResponseEntity<Player> registerPlayer(@PathVariable String username, @RequestParam String email, @RequestParam String locale) {
		
		Player response = service.registerPlayer(username, email, locale);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Player> updatePlayerDetails(@RequestBody Player playerData) {
		
		Player response = service.updatePlayerDetails(playerData);
			
		return new ResponseEntity<>(response, updateHttpHeaders(), HttpStatus.ACCEPTED);
	}

	@Override
	public void setPlayerPassword(@PathVariable String username, @RequestParam String activationCode, @RequestParam String newPassword) {
		
		service.setPlayerPassword(username, activationCode, newPassword);
	}

	@Override
	public Session getActiveSession() {
		
		return sessionService.getActiveSession();
	}

	@Override
	public ResponseEntity<Session> createSession(@PathVariable String username, @RequestParam String password, @RequestParam String clientType, @RequestParam String ipAddress) {
		
		Player loggedUser = service.login(username, password);
		
		Session response = sessionService.createSession(loggedUser, clientType, ipAddress);
		
		return new ResponseEntity<>(response, updateHttpHeaders(), HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<Session> setActiveBeing(@PathVariable Long beingCode) {

		Session session = service.setActiveBeing(beingCode);
		
		return new ResponseEntity<>(session, updateHttpHeaders(), HttpStatus.ACCEPTED);
	}
		
	
	@Override
	public ResponseEntity<Player> createBeing(@RequestParam String beingClass, @RequestParam String beingName,
			@RequestParam String worldName, @RequestParam Integer placeCode) {
		
		Player response = service.createBeing(beingClass, beingName, worldName, placeCode);
		
		return new ResponseEntity<>(response, updateHttpHeaders(), HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<Player> destroyBeing(@PathVariable Long beingCode) {
		
		Player response = service.destroyBeing(beingCode);
		
		return new ResponseEntity<>(response, updateHttpHeaders(), HttpStatus.ACCEPTED);
	}
	
	private HttpHeaders updateHttpHeaders() {
		
		HttpHeaders header = new HttpHeaders();
		header.add(CommonConstants.AUTH_TOKEN_HEADER,
				String.valueOf(
						SecurityContextHolder.getContext().getAuthentication().getCredentials()
				));
		
		return header;
	}
}
