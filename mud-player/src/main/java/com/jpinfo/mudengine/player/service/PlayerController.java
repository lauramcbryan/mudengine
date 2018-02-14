package com.jpinfo.mudengine.player.service;

import java.util.Date;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
	public Player getPlayerDetails(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable String username) {
		
		Player response = null;
		
		if (canAccess(authToken, username)) {
		
			MudPlayer dbPlayer = repository.findByUsername(username);
		
			if (dbPlayer!=null) {
				response = PlayerHelper.buildPlayer(dbPlayer);
			} else {
				throw new EntityNotFoundException("Player entity not found");
			}
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return response;
	}

	@Override
	public ResponseEntity<Player> registerPlayer(@PathVariable String username, @RequestParam String email, @RequestParam String language) {
		
		ResponseEntity<Player> response = null;

		try {
			MudPlayer newPlayer = new MudPlayer();
			newPlayer.setUsername(username);
			newPlayer.setPassword(PlayerHelper.generatePassword());
			newPlayer.setEmail(email);
			newPlayer.setLanguage(language);
			newPlayer.setStatus(Player.STATUS_PENDING);
			
			// Persist to have the playerId
			MudPlayer createdPlayer = repository.save(newPlayer);
			
			response = new ResponseEntity<Player>(PlayerHelper.buildPlayer(createdPlayer), HttpStatus.CREATED);
			
			// TODO: Send the password by email
			
			
		} catch(DataIntegrityViolationException e) {
			
			if (e.getCause() instanceof ConstraintViolationException) {
				
				ConstraintViolationException constraintException = (ConstraintViolationException)e.getCause();
				
				if (constraintException.getConstraintName().equals("mud_player_username_key")) {
					throw new IllegalParameterException("Username already in use");
				}
			}
			
			throw e;
		}
		
		
		return response;
	}

	@Override
	public ResponseEntity<Player> updatePlayerDetails(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable String username, @RequestBody PlayerSimpleData playerData) {
		
		Player response = null;
		
		if (canAccess(authToken, username)) {
		
			MudPlayer dbPlayer = repository.findByUsername(username);
			
			if (dbPlayer!=null) {
			
				dbPlayer.setLanguage(playerData.getLanguage());
				dbPlayer.setName(playerData.getName());
				dbPlayer.setCountry(playerData.getCountry());
				dbPlayer.setLanguage(playerData.getLanguage());
				
				MudPlayer changedPlayer = repository.save(dbPlayer);
				
				response = PlayerHelper.buildPlayer(changedPlayer);
			} else throw new EntityNotFoundException("Player entity not found");
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return new ResponseEntity<Player>(response, HttpStatus.ACCEPTED);
	}

	@Override
	public void setPlayerPassword(@PathVariable String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
		
		MudPlayer dbPlayer = repository.findByUsername(username);
		
		if (dbPlayer!=null) {
			
			if (dbPlayer.getPassword().equals(oldPassword)) {
				
				dbPlayer.setPassword(newPassword);
				dbPlayer.setStatus(Player.STATUS_ACTIVE);
				
				repository.save(dbPlayer);
				
			} else {
				throw new IllegalParameterException("Old password doesn't match");
			}
		} else throw new EntityNotFoundException("Player entity not found");
		
	}

	@Override
	public void deletePlayer(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable String username) {
		
		if (canAccess(authToken, username)) {
			MudPlayer dbPlayer = repository.findByUsername(username);
			
			if (dbPlayer!=null) {
				repository.delete(dbPlayer);
			}
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
	}

	@Override
	public Session getActiveSession(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable String username) {
		
		Session session = null;
		
		if (canAccess(authToken, username)) {
		
			List<MudSession> lstSessions = sessionRepository.findActiveSession(username);
			
			if (!lstSessions.isEmpty()) {
				session = PlayerHelper.buildSession(lstSessions.get(0));
			} else {
				throw new EntityNotFoundException("Session not found");
			}
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return session;
	}

	@Override
	public ResponseEntity<Session> createSession(@PathVariable String username, @RequestParam String password) {
		
		ResponseEntity<Session> response = null;
		
		MudPlayer dbPlayer = repository.findByUsername(username);
		
		if (dbPlayer!=null) {
			
			if (dbPlayer.getPassword().equals(password)) {
				
				switch(dbPlayer.getStatus()) {
				
					case Player.STATUS_ACTIVE: {
					
						MudSession dbSession = new MudSession();
						
						dbSession.setPlayer(dbPlayer);
						dbSession.setSessionStart(new Date());
						
						MudSession createdDbSession = sessionRepository.save(dbSession);
						
						Session session = PlayerHelper.buildSession(createdDbSession);
						
						
						// Build the jwts token
						String token = TokenService.buildToken(username, dbPlayer.getPlayerId(), dbPlayer.getLanguage());
						
						HttpHeaders header = new HttpHeaders();
						header.add(TokenService.HEADER_TOKEN, token);
						
						response = new ResponseEntity<Session>(session, header, HttpStatus.CREATED);
						break;
					}
					case Player.STATUS_PENDING: {
						throw new IllegalParameterException("You must first change your password");
					}
					default: {
						throw new IllegalParameterException("You cannot login at this moment.  Contact the system administrator");
					}
				}
				
			} else {
				throw new IllegalParameterException("Username/password invalid");
			}
		} else {
			throw new IllegalParameterException("Username/password invalid");
		}
		
		return response;
	}

	@Override
	public void deleteActiveSession(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable String username) {

		if (canAccess(authToken, username)) {
			List<MudSession> lstSessions = sessionRepository.findActiveSession(username);
			
			for(MudSession curSession: lstSessions) {
				
				curSession.setSessionEnd(new Date());
				
				sessionRepository.save(curSession);
			}
			
		} else {
			throw new IllegalParameterException("No access to that username");
		}
	}
	
	private boolean canAccess(String authToken, String username) {
		
		String authUserName = TokenService.getUsernameFromToken(authToken);
		
		return ((username.equals(authUserName)) || (TokenService.INTERNAL_ACCOUNT.equals(authUserName)));
		
	}
}
