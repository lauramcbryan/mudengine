package com.jpinfo.mudengine.action.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.action.model.MudAction;


public interface MudActionRepository extends CrudRepository<MudAction, Long> {

	@Query("select a from MudAction a where a.currState=0 "
			+ "and not exists (select 1 from MudAction o where o.actorCode=a.actorCode and o.currState=1)")
	List<MudAction> findStartableActions();
	
	List<MudAction> findByIssuerCode(Long issuerCode);
	
	@Query("select a from MudAction a where a.currState=1 and a.endTurn <= ?")
	List<MudAction> findFinishedActions(Long curTurn);
	
	MudAction findFirstOneByCurrStateAndActorCode(Integer currState, Long actorCode);
}
