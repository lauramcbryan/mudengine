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
		
			MudPlayer dbPlayer = repository.findByUsername(username)
					.orElseThrow(() -> new EntityNotFoundException("Player entity not found"));
		
			response = PlayerHelper.buildPlayer(dbPlayer);
			
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
		
			MudPlayer dbPlayer = repository.findByUsername(username)
					.orElseThrow(() -> new EntityNotFoundException("Player entity not found"));
			
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
			HttpHeaders header = PlayerHelper.updateAuthHeaders(authToken, changedPlayer, null);
			response = new ResponseEntity<Player>(changedPlayer, header, HttpStatus.ACCEPTED);
				
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return response;
	}

	@Override
	public void setPlayerPassword(@PathVariable String username, @RequestParam String activationCode, @RequestParam String newPassword) {
		
		MudPlayer dbPlayer = repository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException("Player entity not found"));
		
		if (dbPlayer.getPassword().equals(activationCode)) {
			
			dbPlayer.setPassword(newPassword);
			dbPlayer.setStatus(Player.STATUS_ACTIVE);
			
			repository.save(dbPlayer);
			
		} else {
			throw new IllegalParameterException("Activation Code doesn't match");
		}
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
		
		MudPlayer dbPlayer = repository.findByUsernameAndPassword(username, password)
				.orElseThrow(() -> new IllegalParameterException("Username/password invalid"));
		
		switch(dbPlayer.getStatus()) {
		
			case Player.STATUS_ACTIVE: {
				
				// Find all the active sessions and terminate them
				List<MudSession> lstSessions = sessionRepository.findActiveSession(username);
				
				lstSessions.forEach(d -> {
					
					d.setSessionEnd(new Date());
					sessionRepository.save(d);
				});
			
				// Creates a new session
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
		
		return response;
	}
	
	private boolean canAccess(String authToken, String username) {
		
		String authUserName = TokenService.getUsernameFromToken(authToken);
		
		return ((username.equals(authUserName)) || (TokenService.INTERNAL_ACCOUNT.equals(authUserName)));
		
	}

	@Override
	public ResponseEntity<Session> setActiveBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable String username, @PathVariable Long beingCode) {
		
		return updateBeingSession(authToken, username, Optional.of(beingCode));
	}
		
	
	private ResponseEntity<Session> updateBeingSession(String authToken, String username, Optional<Long> beingCode) {
		
		ResponseEntity<Session> response = null;
		
		if (canAccess(authToken, username)) {
		
			List<MudSession> lstSessions = sessionRepository.findActiveSession(username);
			
			if (!lstSessions.isEmpty()) {

				// Get the first session data found
				MudSession dbSession = lstSessions.get(0);
				
				// Erases the beingCode
				dbSession.setBeingCode(null);
				
				beingCode.ifPresent(d -> {

					// Find the being record for this player					
					dbSession.getPlayer().getBeingList().stream()
						.filter(e -> e.getId().getBeingCode().equals(beingCode.get()))
						.findFirst()
						.ifPresent(f -> {
							// Update the last time played
							f.setLastPlayed(new Date(System.currentTimeMillis()));
							
							// Set the beingCode in the session object
							dbSession.setBeingCode(beingCode.get());
					});
				});
				
				sessionRepository.save(dbSession);
								
				// Retrieves the Session object
				Session sessionData = PlayerHelper.buildSession(dbSession);
				
				// Retrieves the player object
				Player playerData = PlayerHelper.buildPlayer(dbSession.getPlayer());
				
				
				// Update the authToken
				HttpHeaders header = PlayerHelper.updateAuthHeaders(authToken, playerData, sessionData);
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
	public ResponseEntity<Player> createBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable String username, @RequestParam String beingClass, @RequestParam String beingName,
			@RequestParam String worldName, @RequestParam Integer placeCode) {
		
		ResponseEntity<Player> response = null;
		
		if (canAccess(authToken, username)) {
			
			MudPlayer dbPlayer = repository.findByUsername(username)
					.orElseThrow(() -> new EntityNotFoundException("Player not found"));
			
			// Create the being
			ResponseEntity<Being> beingResponse = 
				this.beingClient.createPlayerBeing(authToken,
						dbPlayer.getPlayerId(), beingClass, 
						worldName, placeCode, beingName);
			
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
			
			// Update the dbPlayer being list
			dbPlayer.getBeingList().add(dbBeing);
			
			// Save the dbPlayer
			MudPlayer updatedPlayerData = repository.save(dbPlayer);
			
			Player playerData = PlayerHelper.buildPlayer(updatedPlayerData);

			// Assembling the response
			HttpHeaders header = PlayerHelper.updateAuthHeaders(authToken, playerData, null);
			response = new ResponseEntity<Player>(playerData, header, HttpStatus.ACCEPTED);
			
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return response;
	}

	@Override
	public ResponseEntity<Player> destroyBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable String username, @PathVariable Long beingCode) {
		
		ResponseEntity<Player> response = null;
		
		if (canAccess(authToken, username)) {
			
			// Find the player data
			Optional<MudPlayer> dbPlayer = repository.findByUsername(username);
			
			dbPlayer.ifPresent(d -> {
				
				// Check if the selected being exists and it's associated to the player
				d.getBeingList().stream()
					.filter(e -> e.getId().getBeingCode().equals(beingCode))
					.findFirst()
					.orElseThrow(() -> new IllegalParameterException("Being Unknown"));
				
				// Remove selected being from the list
				d.getBeingList().removeIf(e -> e.getId().getBeingCode().equals(beingCode));

				// Destroy the selected being at being service
				beingClient.destroyBeing(authToken, beingCode);
				
				// Update the playerData
				repository.save(d);
				
				// If the being is the currenly selected
				if (TokenService.getBeingCodeFromToken(authToken).equals(beingCode)) {
					
					// Clear the beingCode from the token
					updateBeingSession(authToken, username, Optional.empty());
				}
				
				
			});
			
			// Get the session data
			Session sessionData = getActiveSession(authToken, username);

			// Get the player info again
			Player playerData = getPlayerDetails(authToken, username);

			// Update the authToken
			String token = TokenService.updateToken(authToken, 
					Optional.of(playerData), 
					Optional.of(sessionData));
			

			// Assembling the response
			HttpHeaders header = new HttpHeaders();
			header.add(CommonConstants.AUTH_TOKEN_HEADER, token);
			
			response = new ResponseEntity<Player>(
					playerData, 
					header, HttpStatus.ACCEPTED);
			
		} else {
			throw new IllegalParameterException("No access to that username");
		}
		
		return response;
	}
}
