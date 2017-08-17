package com.jpinfo.mudengine.player.repository;

import java.util.List;

import javax.persistence.OrderBy;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.player.model.MudSession;

public interface SessionRepository extends CrudRepository<MudSession, Long> {
	
	@Query("select a from MudSession a "
			+ "where a.player=(select b from MudPlayer b where b.username=?) "
			+ "and a.sessionEnd is not null ")
	@OrderBy("sessionStart DESC")
	List<MudSession> findActiveSession(String username);

}
