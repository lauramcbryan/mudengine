package com.jpinfo.mudengine.being.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.being.model.MudPlayerSession;

public interface PlayerSessionRepository extends CrudRepository<MudPlayerSession, Long> {
	
	@Query("select u from MudPlayerSession u, MudPlayer p where u.playerId=p.playerId and u.sessionEnd is null and p.login=?")
	MudPlayerSession findActiveByPlayerLogin(String login);
	
	@Query("select u from MudPlayerSession u where u.playerId=? and u.sessionEnd is null")
	MudPlayerSession findActiveByPlayerId(Long playerId);

}
