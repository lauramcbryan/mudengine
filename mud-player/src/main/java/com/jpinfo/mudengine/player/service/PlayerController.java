package com.jpinfo.mudengine.player.service;

import java.util.Date;

import java.util.List;
import java.util.Optional;

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

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.service.PlayerService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.player.client.BeingServiceClient;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.model.MudPlayerBeing;
import com.jpinfo.mudengine.player.model.MudSession;
import com.jpinfo.mudengine.player.model.pk.MudPlayerBeingPK;
import com.jpinfo.mudengine.player.repository.PlayerRepository;
import com.jpinfo.mudengine.player.repository.SessionRepository;
import com.jpinfo.mudengine.player.util.PlayerHelper;

@RestController
public class PlayerController implements PlayerService {
	
	@Autowired
	private PlayerRepository repository;
	
	@Autowired
	private SessionRepository sessionRepository;
	
	@Autowired
	private BeingServiceClient beingClient;

	@Override
	public Player getPlayerDetails(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable String username) {
		
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
	public ResponseEntity<Player> registerPlayer(@PathVariable String username, @RequestParam String email, @RequestParam String locale) {
		
		ResponseEntity<Player> response = null;

		try {
			MudPlayer newPlayer = new MudPlayer();
			newPlayer.setUsername(username);
			newPlayer.setPassword(PlayerHelper.generatePassword());
			newPlayer.setEmail(email);
			newPlayer.setLocale(locale);
			newPlayer.setStatus(Player.STATUS_PENDING);
			newPlayer.setCreateDate(new Date());
			
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
	public ResponseEntity<Player> updatePlayerDetails(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable String username, @RequestBody Player playerData) {
		
		ResponseEntity<Player> response = null;
		
		if (canAccess(authToken, username)) {
		
			MudPlayer dbPlayer = repository.findByUsername(username);
			
			if (dbPlayer!=null) {
			
				dbPlayer.setLocale(playerData.getLocale());
				dbPlayer.setUsername(playerData.getUsername());
				
				// If the user is changing the email, the account status goes to PENDING
				if (!dbPlayer.getEmail().equals(playerData.getEmail())) {
					dbPlayer.setEmail(playerData.getEmail());
					dbPlayer.setStatus(Player.STATUS_PENDING);
				}
				
				MudPlayer changedDbPlayer = repository.save(dbPlayer);
				
				Player changedPlayer = PlayerHelper.buildPlayer(changedDbPlayer);
				
				// Update the authToken
				String token = TokenService.updateToken(authToken, Optional.of(changedPlayer), Optional.empty());
				
				HttpHeaders header = new HttpHeaders();
				header.add(CommonConstants.AUTH_TOKEN_HEADER, token);
				
				response = new ResponseEntity<Player>(changedPlayer, header, HttpStatus.ACCEPTED);
				
			} else throw new EntityNotFoundException("Player entity not found");
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return response;
	}

	@Override
	public void setPlayerPassword(@PathVariable String username, @RequestParam String activationCode, @RequestParam String newPassword) {
		
		MudPlayer dbPlayer = repository.findByUsername(username);
		
		if (dbPlayer!=null) {
			
			if (dbPlayer.getPassword().equals(activationCode)) {
				
				dbPlayer.setPassword(newPassword);
				dbPlayer.setStatus(Player.STATUS_ACTIVE);
				
				repository.save(dbPlayer);
				
			} else {
				throw new IllegalParameterException("Activation Code doesn't match");
			}
		} else throw new EntityNotFoundException("Player entity not found");
		
	}

	@Override
	public Session getActiveSession(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable String username) {
		
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
	public ResponseEntity<Session> createSession(@PathVariable String username, @RequestParam String password, @RequestParam String clientType, @RequestParam String ipAddress) {
		
		ResponseEntity<Session> response = null;
		
		MudPlayer dbPlayer = repository.findByUsername(username);
		
		if (dbPlayer!=null) {
			
			if (dbPlayer.getPassword().equals(password)) {
				
				switch(dbPlayer.getStatus()) {
				
					case Player.STATUS_ACTIVE: {
					
						MudSession dbSession = new MudSession();
						
						dbSession.setPlayer(dbPlayer);
						dbSession.setSessionStart(new Date());
						dbSession.setClientType(clientType);
						dbSession.setIpAddress(ipAddress);
						
						MudSession createdDbSession = sessionRepository.save(dbSession);
						
						Session sessionData = PlayerHelper.buildSession(createdDbSession);
						
						Player playerData = PlayerHelper.buildPlayer(dbPlayer);
						
						
						// Build the jwts token
						String token = TokenService.buildToken(username, playerData, sessionData);
						
						HttpHeaders header = new HttpHeaders();
						header.add(CommonConstants.AUTH_TOKEN_HEADER, token);
						
						response = new ResponseEntity<Session>(sessionData, header, HttpStatus.CREATED);
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
	
	private boolean canAccess(String authToken, String username) {
		
		String authUserName = TokenService.getUsernameFromToken(authToken);
		
		return ((username.equals(authUserName)) || (TokenService.INTERNAL_ACCOUNT.equals(authUserName)));
		
	}

	@Override
	public ResponseEntity<Session> setActiveBeing(@RequestHeader String authToken, @PathVariable String username, @PathVariable Long beingCode) {
		
		return updateBeingSession(authToken, username, Optional.of(beingCode));
	}
		
	
	private ResponseEntity<Session> updateBeingSession(String authToken, String username, Optional<Long> beingCode) {
		
		ResponseEntity<Session> response = null;
		
		if (canAccess(authToken, username)) {
		
			List<MudSession> lstSessions = sessionRepository.findActiveSession(username);
			
			if (!lstSessions.isEmpty()) {

				// Get the first session data found
				MudSession dbSession = lstSessions.get(0);
				
				// Set the beingCode
				dbSession.setBeingCode(beingCode.get());
				
				if (beingCode.isPresent()) {

					// Find the being record for this player
					for(MudPlayerBeing curBeing: dbSession.getPlayer().getBeingList()) {
						
						if (curBeing.getId().getBeingCode().equals(beingCode.get())) {
							
							// Update the last time played
							curBeing.setLastPlayed(new Date(System.currentTimeMillis()));
						}
					}
				}
				
				sessionRepository.save(dbSession);
								
				// Update the JWT Token
				Session sessionData = PlayerHelper.buildSession(dbSession);
				
				// Update the authToken
				String token = TokenService.updateToken(authToken, Optional.empty(), Optional.of(sessionData));
				
				HttpHeaders header = new HttpHeaders();
				header.add(CommonConstants.AUTH_TOKEN_HEADER, token);
				
				response = new ResponseEntity<Session>(sessionData, header, HttpStatus.ACCEPTED);
				
			} else {
				throw new EntityNotFoundException("Session not found");
			}
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return response;
	}
	
	@Override
	public ResponseEntity<Session> createBeing(@RequestHeader String authToken, @PathVariable String username, @RequestParam String beingClass, @RequestParam String beingName,
			@RequestParam String worldName, @RequestParam Integer placeCode) {
		
		ResponseEntity<Session> response = null;
		
		if (canAccess(authToken, username)) {
			
			MudPlayer dbPlayer = repository.findByUsername(username);
			
			if (dbPlayer!=null) {
				
				// Create the being
				ResponseEntity<Being> beingResponse = 
					this.beingClient.createBeing(authToken, 
							Being.BEING_TYPE_PLAYER, beingClass, 
							worldName, placeCode, 
							Optional.empty(), Optional.of(dbPlayer.getPlayerId()),
							Optional.of(beingName));

				if (beingResponse.getStatusCode().equals(HttpStatus.CREATED)) {
					
					Being being = beingResponse.getBody();

					// Update the dbPlayer entity
					MudPlayerBeing dbBeing = new MudPlayerBeing();
					MudPlayerBeingPK pk = new MudPlayerBeingPK();
					dbBeing.setId(pk);
					
					pk.setPlayerId(dbPlayer.getPlayerId());
					pk.setBeingCode(being.getBeingCode());
					
					dbBeing.setBeingName(beingName);
					dbBeing.setBeingClass(being.getBeingClass().getName());
					dbBeing.setLastPlayed(new Date(System.currentTimeMillis()));
					
					// Update the dbPlayer entity
					dbPlayer.getBeingList().add(dbBeing);
					
					// Save the dbPlayer
					repository.save(dbPlayer);
					
				} else {
					throw new IllegalParameterException("Couldn't create the being: " + beingResponse.getStatusCode().getReasonPhrase());					
				}
			}
			else {
				throw new EntityNotFoundException("Player not found");
			}
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return response;
	}

	@Override
	public ResponseEntity<Session> destroyBeing(String authToken, String username, Long beingCode) {
		
		ResponseEntity<Session> response = null;
		
		if (canAccess(authToken, username)) {

			// Destroy the selected being
			beingClient.destroyBeing(authToken, beingCode);
			
			// If the being is the currenly selected
			if (TokenService.getBeingCodeFromToken(authToken).equals(beingCode)) {
				
				// Clear the beingCode from the token
				response = updateBeingSession(authToken, username, Optional.empty());
				
			} else {
				
				// Just echoes the current session back
				response = new ResponseEntity<Session>(
						TokenService.getSessionDataFromToken(authToken), 
						null, HttpStatus.ACCEPTED);
			}
			
			
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return response;
	}
}
