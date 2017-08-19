package com.jpinfo.mudengine.player.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.player.model.MudSession;

public interface SessionRepository extends CrudRepository<MudSession, Long> {
	
	@Query("select a from MudSession a " 
			+ "inner join a.player b "
			+ "where b.username=? "
			+ "and a.sessionEnd is null order by a.sessionStart desc ")
	List<MudSession> findActiveSession(String username);

}
