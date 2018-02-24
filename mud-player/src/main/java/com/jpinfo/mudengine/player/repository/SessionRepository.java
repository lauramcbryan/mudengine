package com.jpinfo.mudengine.player.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jpinfo.mudengine.player.model.MudSession;

public interface SessionRepository extends CrudRepository<MudSession, Long> {
	
	@Query("select a from MudSession a " 
			+ "inner join a.player b "
			+ "where b.username=:username "
			+ "and a.sessionEnd is null order by a.sessionStart desc ")
	List<MudSession> findActiveSession(@Param(value="username") String username);

}
