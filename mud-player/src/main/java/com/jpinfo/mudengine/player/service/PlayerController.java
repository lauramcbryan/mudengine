package com.jpinfo.mudengine.player.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.PlayerSimpleData;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.service.PlayerService;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.model.MudSession;
import com.jpinfo.mudengine.player.repository.PlayerRepository;
import com.jpinfo.mudengine.player.repository.SessionRepository;
import com.jpinfo.mudengine.player.util.PlayerHelper;

@RestController
public class PlayerController implements PlayerService {
	
	@Autowired
	private PlayerRepository repository;
	
	@Autowired
	private SessionRepository sessionRepository;

	@Override
	public Player getPlayerDetails(@PathVariable String username) {
		
		Player response = null;
		
		MudPlayer dbPlayer = repository.findByUsername(username);
		
		if (dbPlayer!=null) {
			
			response = PlayerHelper.buildPlayer(dbPlayer);
		} else {
			throw new EntityNotFoundException("Player entity not found");
		}
		
		return response;
	}

	@Override
	public ResponseEntity<Player> registerPlayer(@PathVariable String username, @RequestParam String email) {
		
		MudPlayer newPlayer = new MudPlayer();
		newPlayer.setUsername(username);
		newPlayer.setPassword(PlayerHelper.generatePassword());
		newPlayer.setEmail(email);
		newPlayer.setStatus(Player.STATUS_PENDING);
		
		// Persist to have the playerId
		MudPlayer createdPlayer = repository.save(newPlayer);
		
		// TODO: Send the password by email
		
		
		return new ResponseEntity<Player>(PlayerHelper.buildPlayer(createdPlayer), HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Player> updatePlayerDetails(@PathVariable String username, @RequestBody PlayerSimpleData playerData) {
		
		Player response = null;
		
		MudPlayer dbPlayer = repository.findByUsername(username);
		
		if (dbPlayer!=null) {
			
			dbPlayer.setLanguage(playerData.getLanguage());
			dbPlayer.setName(playerData.getName());
			dbPlayer.setCountry(playerData.getCountry());
			dbPlayer.setLanguage(playerData.getLanguage());
			
			MudPlayer changedPlayer = repository.save(dbPlayer);
			
			response = PlayerHelper.buildPlayer(changedPlayer);
			
		} else throw new EntityNotFoundException("Player entity not found");
		
		
		return new ResponseEntity<Player>(response, HttpStatus.ACCEPTED);
	}

	@Override
	public void setPlayerPassword(@PathVariable String username, @PathVariable String oldPassword, @RequestParam String newPassword) {
		
		MudPlayer dbPlayer = repository.findByUsername(username);
		
		if (dbPlayer!=null) {
			
			if (dbPlayer.getPassword().equals(oldPassword)) {
				
				dbPlayer.setPassword(newPassword);
				
				repository.save(dbPlayer);
				
			} else {
				throw new IllegalParameterException("Old password doesn't match");
			}
		} else throw new EntityNotFoundException("Player entity not found");
		
	}

	@Override
	public void deletePlayer(@PathVariable String username) {
		
		MudPlayer dbPlayer = repository.findByUsername(username);
		
		if (dbPlayer!=null) {
			repository.delete(dbPlayer);
		}
		
	}

	@Override
	public Session getActiveSession(@PathVariable String username) {
		
		Session session = null;
		
		List<MudSession> lstSessions = sessionRepository.findActiveSession(username);
		
		if (!lstSessions.isEmpty()) {
			session = PlayerHelper.buildSession(lstSessions.get(0));
		} else {
			throw new EntityNotFoundException("Session not found");
		}
		
		return session;
	}

	@Override
	public ResponseEntity<Session> createSession(@PathVariable String username, @RequestParam String password) {
		
		ResponseEntity<Session> response = null;
		
		MudPlayer dbPlayer = repository.findByUsername(username);
		
		if (dbPlayer!=null) {
			
			if (dbPlayer.getPassword().equals(password)) {
				MudSession dbSession = new MudSession();
				
				dbSession.setPlayer(dbPlayer);
				dbSession.setSessionStart(new Date());
				
				MudSession createdDbSession = sessionRepository.save(dbSession);
				
				Session session = PlayerHelper.buildSession(createdDbSession);
				
				
				
				// Build the jwts token
				String token = TokenService.buildToken(username, dbPlayer.getPlayerId());
				
				HttpHeaders header = new HttpHeaders();
				header.add(TokenService.HEADER_TOKEN, token);
				
				response = new ResponseEntity<Session>(session, header, HttpStatus.CREATED);
				
			} else {
				throw new IllegalArgumentException("Username/password invalid");
			}
		} else {
			throw new IllegalArgumentException("Username/password invalid");
		}
		
		return response;
	}

	@Override
	public void deleteActiveSession(@PathVariable String username) {
		
		List<MudSession> lstSessions = sessionRepository.findActiveSession(username);
		
		for(MudSession curSession: lstSessions) {
			
			curSession.setSessionEnd(new Date());
			
			sessionRepository.save(curSession);
			
		}
	}
}
