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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.AccessDeniedException;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.service.PlayerService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
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
	
	@Autowired
	private TokenService tokenService;

	@Override
	public Player getPlayerDetails(@PathVariable String username) {
		
		Player response = null;
		
		if (canAccess(username)) {
		
			MudPlayer dbPlayer = repository.findByUsername(username)
					.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
		
			response = PlayerHelper.buildPlayer(dbPlayer);
			
		} else {
			throw new AccessDeniedException(LocalizedMessages.PLAYER_ACCESS_DENIED);
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
			
			response = new ResponseEntity<>(PlayerHelper.buildPlayer(createdPlayer), HttpStatus.CREATED);
			
			// TODO: Send the password by email
			
			
		} catch(DataIntegrityViolationException e) {
			
			if (e.getCause() instanceof ConstraintViolationException) {
				
				ConstraintViolationException constraintException = (ConstraintViolationException)e.getCause();
				
				if (constraintException.getConstraintName().equals("mud_player_username_key")) {
					throw new IllegalParameterException(LocalizedMessages.PLAYER_NAME_IN_USE);
				}
			}
			
			throw e;
		}
		
		
		return response;
	}

	@Override
	public ResponseEntity<Player> updatePlayerDetails(@PathVariable String username, @RequestBody Player playerData) {
		
		ResponseEntity<Player> response = null;
		
		if (canAccess(username)) {
		
			MudPlayer dbPlayer = repository.findByUsername(username)
					.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
			
			dbPlayer.setLocale(playerData.getLocale());
			dbPlayer.setUsername(playerData.getUsername());
			
			// If the user is changing the email, the account status goes to PENDING
			if (!dbPlayer.getEmail().equals(playerData.getEmail())) {
				dbPlayer.setEmail(playerData.getEmail());
				dbPlayer.setStatus(Player.STATUS_PENDING);
			}
			
			MudPlayer changedDbPlayer = repository.save(dbPlayer);
			
			Player changedPlayer = PlayerHelper.buildPlayer(changedDbPlayer);
			
			String authToken = (String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
			
			
			// Update the authToken
			HttpHeaders header = updateAuthHeaders(authToken, changedPlayer, null);
			response = new ResponseEntity<>(changedPlayer, header, HttpStatus.ACCEPTED);
				
		} else {
			throw new AccessDeniedException(LocalizedMessages.PLAYER_ACCESS_DENIED);
		}
		
		return response;
	}

	@Override
	public void setPlayerPassword(@PathVariable String username, @RequestParam String activationCode, @RequestParam String newPassword) {
		
		MudPlayer dbPlayer = repository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
		
		if (dbPlayer.getPassword().equals(activationCode)) {
			
			dbPlayer.setPassword(newPassword);
			dbPlayer.setStatus(Player.STATUS_ACTIVE);
			
			repository.save(dbPlayer);
			
		} else {
			throw new IllegalParameterException(LocalizedMessages.PLAYER_ACTIVATION_MISMATCH);
		}
	}

	@Override
	public Session getActiveSession(@PathVariable String username) {
		
		Session session = null;
		
		if (canAccess(username)) {
		
			Optional<MudSession> dbSession = sessionRepository.findActiveSession(username);
			
			if (dbSession.isPresent()) {
				
				session = PlayerHelper.buildSession(dbSession.get());
			} else {
				throw new EntityNotFoundException(LocalizedMessages.SESSION_NOT_FOUND);
			}
		} else {
			throw new AccessDeniedException(LocalizedMessages.PLAYER_ACCESS_DENIED);
		}
		
		return session;
	}

	@Override
	public ResponseEntity<Session> createSession(@PathVariable String username, @RequestParam String password, @RequestParam String clientType, @RequestParam String ipAddress) {
		
		ResponseEntity<Session> response = null;
		
		MudPlayer dbPlayer = repository.findByUsernameAndPassword(username, password)
				.orElseThrow(() -> new IllegalParameterException(LocalizedMessages.PLAYER_LOGIN_ERROR));
		
		switch(dbPlayer.getStatus()) {
		
			case Player.STATUS_ACTIVE:
				
				// Find all the active sessions and terminate them
				List<MudSession> lstSessions = sessionRepository.findAllActiveSession(username);
				
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
				String token = tokenService.buildToken(username, 
						Optional.of(playerData), 
						Optional.of(sessionData));
				
				HttpHeaders header = new HttpHeaders();
				header.add(CommonConstants.AUTH_TOKEN_HEADER, token);
				
				response = new ResponseEntity<>(sessionData, header, HttpStatus.CREATED);
				break;
			
			case Player.STATUS_PENDING: 
				throw new IllegalParameterException(LocalizedMessages.PLAYER_CHANGE_PASSWORD);
			
			default: 
				throw new IllegalParameterException(LocalizedMessages.PLAYER_NO_LOGIN);
		}
		
		return response;
	}
	
	private boolean canAccess(String username) {
		
		String authUserName = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		return ((username.equals(authUserName)) || (TokenService.INTERNAL_ACCOUNT.equals(authUserName)));
		
	}

	@Override
	public ResponseEntity<Session> setActiveBeing(@PathVariable String username, @PathVariable Long beingCode) {
		
		return updateBeingSession(username, Optional.of(beingCode));
	}
		
	
	private ResponseEntity<Session> updateBeingSession(String username, Optional<Long> beingCode) {
		
		ResponseEntity<Session> response = null;
		
		if (canAccess(username)) {
		
			MudSession dbSession = 
					sessionRepository.findActiveSession(username)
					.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.SESSION_NOT_FOUND));
			
			Being selectedBeing= null;
			
			if (beingCode.isPresent()) {
				
				Long activeBeingCode = beingCode.get();

				// Set the beingCode in the session object					
				dbSession.setBeingCode(activeBeingCode);
				
				// Find the being record for this player					
				dbSession.getPlayer().getBeingList().stream()
					.filter(e -> e.getId().getBeingCode().equals(activeBeingCode))
					.findFirst()
					.ifPresent(f -> 
						// Update the last time played
						f.setLastPlayed(new Date(System.currentTimeMillis()))
					);
				
				selectedBeing = beingClient.getBeing(activeBeingCode);
				
			} else {
				
				// Erases the beingCode
				dbSession.setBeingCode(null);
			}
			
			// Update in database				
			sessionRepository.save(dbSession);
			
			// Converts the dbSession object to Session
			Session sessionData = PlayerHelper.buildSession(dbSession);
			
			if (selectedBeing!=null)
				sessionData.setCurWorldName(selectedBeing.getCurWorld());
			
			
			// Retrieves the player object
			Player playerData = PlayerHelper.buildPlayer(dbSession.getPlayer());
			
			String authToken = (String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
			
			// Update the authToken
			HttpHeaders header = updateAuthHeaders(authToken, playerData, sessionData);
			response = new ResponseEntity<>(sessionData, header, HttpStatus.ACCEPTED);
			
		} else {
			throw new AccessDeniedException(LocalizedMessages.PLAYER_ACCESS_DENIED);
		}
		
		return response;
	}
	
	@Override
	public ResponseEntity<Player> createBeing(@PathVariable String username, @RequestParam String beingClass, @RequestParam String beingName,
			@RequestParam String worldName, @RequestParam Integer placeCode) {
		
		ResponseEntity<Player> response = null;
		
		if (canAccess(username)) {
			
			MudPlayer dbPlayer = repository.findByUsername(username)
					.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
			
			// Create the being
			ResponseEntity<Being> beingResponse = 
				this.beingClient.createPlayerBeing(
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
			
			String authToken = (String)SecurityContextHolder.getContext().getAuthentication().getCredentials();

			// Assembling the response
			HttpHeaders header = updateAuthHeaders(authToken, playerData, null);
			response = new ResponseEntity<>(playerData, header, HttpStatus.ACCEPTED);
			
		} else {
			throw new AccessDeniedException(LocalizedMessages.PLAYER_ACCESS_DENIED);
		}
		
		return response;
	}

	@Override
	public ResponseEntity<Player> destroyBeing(@PathVariable String username, @PathVariable Long beingCode) {
		
		ResponseEntity<Player> response = null;
		
		if (canAccess(username)) {
			
			// Find the player data
			Optional<MudPlayer> dbPlayer = repository.findByUsername(username);
			
			dbPlayer.ifPresent(d -> {
				
				// Check if the selected being exists and it's associated to the player
				if (d.getBeingList().stream()
						.noneMatch(e -> 
							e.getId().getBeingCode().equals(beingCode))) {
					
					throw new IllegalParameterException(LocalizedMessages.BEING_NOT_FOUND);
				}
				
				// Remove selected being from the list
				d.getBeingList().removeIf(e -> e.getId().getBeingCode().equals(beingCode));

				// Destroy the selected being at being service
				beingClient.destroyBeing(beingCode);
				
				// Update the playerData
				repository.save(d);
				
				// If the being is the currenly selected
				MudUserDetails uDetails = (MudUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
				
				
				uDetails.getSessionData().ifPresent(e -> {
					
					if (beingCode.equals(e.getBeingCode())) {
						// Clear the beingCode from the token
						updateBeingSession(username, Optional.empty());						
					}
				});
			});
			
			// Get the session data
			Session sessionData = getActiveSession(username);

			// Get the player info again
			Player playerData = getPlayerDetails(username);
			
			String authToken = (String)SecurityContextHolder.getContext().getAuthentication().getCredentials();

			// Update the authToken
			String token = tokenService.updateToken(authToken, 
					Optional.of(playerData), 
					Optional.of(sessionData));
			

			// Assembling the response
			HttpHeaders header = new HttpHeaders();
			header.add(CommonConstants.AUTH_TOKEN_HEADER, token);
			
			response = new ResponseEntity<>(
					playerData, 
					header, HttpStatus.ACCEPTED);
			
		} else {
			throw new AccessDeniedException(LocalizedMessages.PLAYER_ACCESS_DENIED);
		}
		
		return response;
	}
	
	private HttpHeaders updateAuthHeaders(String originalToken, Player playerData, Session sessionData ) {
		
		// Update the authToken
		String token = tokenService.updateToken(originalToken, 
				Optional.ofNullable(playerData), 
				Optional.ofNullable(sessionData));
		

		// Assembling the response
		HttpHeaders header = new HttpHeaders();
		header.add(CommonConstants.AUTH_TOKEN_HEADER, token);

		
		return header;
	}	
}
