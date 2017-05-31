package com.jpinfo.mudengine.being.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jpinfo.mudengine.being.model.MudPlayer;
import com.jpinfo.mudengine.being.model.MudPlayerSession;
import com.jpinfo.mudengine.being.repository.PlayerRepository;
import com.jpinfo.mudengine.being.repository.PlayerSessionRepository;

@RestController
@RequestMapping("/session")
public class SessionController {

	@Autowired
	private PlayerRepository playerRepo;
	
	@Autowired
	private PlayerSessionRepository sessionRepo;
	
	@RequestMapping(method=RequestMethod.PUT, value="/{login}")
	public MudPlayerSession login(@PathVariable String login, @RequestParam String password) {

		MudPlayerSession session = null;
		MudPlayer player = playerRepo.findByLogin(login);
		
		if (player!=null) {
			
			// Check password
			if (player.getPassword().equals(password)) {
		
				// Check if there´s an active session
				session = sessionRepo.findActiveByPlayerLogin(login);
		
				if (session==null) {
					session = new MudPlayerSession();
					session.setPlayerId(player.getPlayerId());
					session.setSessionStart(new Date());
					
					session.setCountry("BR");
					
					session = sessionRepo.save(session);
					
				} else {
					// another session is in progress; returning the existing
				}
			} else {
				// wrong password; login denied
			}
		} else {
			// player unknown; login denied
		}
		
		return session;
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{login}")
	public void logoff(@PathVariable String login) {
		
		MudPlayerSession session = sessionRepo.findActiveByPlayerLogin(login);
		
		if (session!=null) {
			
			session.setSessionEnd(new Date());
			
			sessionRepo.save(session);
		}
	}
}
