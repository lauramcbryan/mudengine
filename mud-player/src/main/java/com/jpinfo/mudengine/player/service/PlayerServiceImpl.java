package com.jpinfo.mudengine.player.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.AccessDeniedException;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.player.client.BeingServiceClient;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.model.MudPlayerBeing;
import com.jpinfo.mudengine.player.model.converter.PlayerConverter;
import com.jpinfo.mudengine.player.model.pk.MudPlayerBeingPK;
import com.jpinfo.mudengine.player.repository.PlayerRepository;
import com.jpinfo.mudengine.player.util.PlayerHelper;

@Service
public class PlayerServiceImpl {

	@Autowired
	private PlayerRepository repository;
	
	@Autowired
	private BeingServiceClient beingClient;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private SessionServiceImpl sessionService;
	
	public Player login(String username, String password) {
		
		return repository.findByUsernameAndPassword(username, password)
				.map(PlayerConverter::convert)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
	}
	
	public Player getPlayerDetails() {
		
		return repository.findById(getActivePlayerId())
				.map(PlayerConverter::convert)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
	}

	public Player registerPlayer(String username, String email, String locale) {
		
		Player response = null;

		try {
			
			MudPlayer newPlayer = new MudPlayer();
			newPlayer.setUsername(username);
			newPlayer.setEmail(email);
			newPlayer.setLocale(locale);
			newPlayer.setStatus(Player.STATUS_PENDING);
			newPlayer.setCreateDate(new Date());
			
			// Check if the email service is configured
			if (mailService.isEnabled())
				
				// In this case, the actual activation code is generated
				// to be sent by email
				newPlayer.setPassword(PlayerHelper.generatePassword());
			else {
				
				// Where email is disabled, the activation code is created
				// with a well-known pattern: pass + <USERNAME>
				
				// The user can activate the account without having the email this way
				
				newPlayer.setPassword("pass-" + username);
			}
			
			// Persist to have the playerId
			response = PlayerConverter.convert(
					repository.save(newPlayer)
					);
			
			if (mailService.isEnabled()) {
				
				// Send the password by email
				mailService.sendActivationEmail(response, newPlayer.getPassword());
			}
			
		} catch(DataIntegrityViolationException e) {
			
			throw new IllegalParameterException(LocalizedMessages.PLAYER_NAME_IN_USE);
		}
		
		
		return response;
	}

	public Player updatePlayerDetails(Player playerData) {
		
		Player response = null;
		
		MudPlayer dbPlayer = repository.findById(getActivePlayerId())
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
		
		dbPlayer.setLocale(playerData.getLocale());
		dbPlayer.setUsername(playerData.getUsername());
		
		// If the user is changing the email, the account status goes to PENDING
		if (!dbPlayer.getEmail().equals(playerData.getEmail())) {
			dbPlayer.setEmail(playerData.getEmail());
			dbPlayer.setStatus(Player.STATUS_PENDING);
		}
			
		response = PlayerConverter.convert(
				repository.save(dbPlayer)
				);
		
		// Update the authToken
		updateAuthContext(response);
		
		return response;
	}

	public void setPlayerPassword(String username, String activationCode, String newPassword) {
		
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
	
	public Player createBeing(String beingClass, String beingName,
			String worldName, Integer placeCode) {
		
		Player response = null;
		
		MudPlayer dbPlayer = repository.findById(getActivePlayerId())
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
		
		// Create the being
		Being being = 
			this.beingClient.createPlayerBeing(
					dbPlayer.getPlayerId(), beingClass, 
					worldName, placeCode, beingName);
		
		// Update the dbPlayer entity
		MudPlayerBeing dbBeing = new MudPlayerBeing();
		dbBeing.setId(new MudPlayerBeingPK());
			
		dbBeing.getId().setPlayerId(dbPlayer.getPlayerId());
		dbBeing.getId().setBeingCode(being.getCode());
		
		dbBeing.setBeingName(being.getName());
		dbBeing.setBeingClass(being.getBeingClass().getCode());
		
		// Update the dbPlayer being list
		dbPlayer.getBeingList().add(dbBeing);
			
		// Save the dbPlayer
		response = PlayerConverter.convert(repository.save(dbPlayer));
		
		updateAuthContext(response);
		
		return response;
	}
	
	public Session setActiveBeing(Long beingCode) {
		
		
		MudPlayer dbPlayer = repository.findById(getActivePlayerId())
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
		
		return dbPlayer.getBeingList().stream()
				.filter(e -> e.getId().getBeingCode().equals(beingCode))
				.map(f -> {
					
					// Update the last time played
					// TODO: Persist it in the database
					f.setLastPlayed(new Date(System.currentTimeMillis()));
			
					// Update the session
					return sessionService.setActiveBeing(beingCode);
				})
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_NOT_FOUND));
	}
	
	public Player destroyBeing(Long destroyedBeingCode) {
		
		return repository.findById(getActivePlayerId())
				.map(dbPlayer -> {

					// Check if the selected being exists and it's associated to the player
					if (dbPlayer.getBeingList().stream()
							.noneMatch(e -> 
								e.getId().getBeingCode().equals(destroyedBeingCode))) {
						
						throw new IllegalParameterException(LocalizedMessages.BEING_NOT_FOUND);
					}

					// Remove selected being from the list
					dbPlayer.getBeingList().removeIf(e -> e.getId().getBeingCode().equals(destroyedBeingCode));

					// Destroy the selected being at being service
					beingClient.destroyBeing(destroyedBeingCode);
				
					Player response = PlayerConverter.convert(
							repository.save(dbPlayer)
							);
				
					updateAuthContext(response);

				
				return response;
			})
			.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLAYER_NOT_FOUND));
	}
	
	private void updateAuthContext(Player playerData) {
		
		String originalToken = String.valueOf(
				SecurityContextHolder.getContext().getAuthentication().getCredentials()
				);
		
		MudUserDetails uDetails = (MudUserDetails)
				SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		
		// Update the authToken
		String token = tokenService.updateToken(originalToken, 
				Optional.ofNullable(playerData),
				uDetails.getSessionData()
				);
		
		SecurityContextHolder.getContext().setAuthentication(
				tokenService.getAuthenticationFromToken(token)
				);
	}
	
	private Long getActivePlayerId() {
		
		MudUserDetails uDetails = (MudUserDetails)
				SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		return uDetails.getPlayerData()
				.map(Player::getPlayerId)
				.orElseThrow(() -> new AccessDeniedException(LocalizedMessages.PLAYER_ACCESS_DENIED));
	}
}
