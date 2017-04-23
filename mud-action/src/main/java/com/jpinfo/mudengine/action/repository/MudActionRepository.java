package com.jpinfo.mudengine.action.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.action.model.MudAction;


public interface MudActionRepository extends CrudRepository<MudAction, Long> {

	@Query("select a from MudAction a where o.cur_state=0 "
			+ "and not exists (select 1 from MudAction o where o.actorCode=u.actorCode and o.cur_state=1)")
	List<MudAction> findStartableActions();
	
	List<MudAction> findByIssuerCode(Integer issuerCode);
	
	@Query("select a from MudAction a where o.cur_state=1 and a.end_turn <= ?")
	List<MudAction> findFinishedActions(Long curTurn);
	
	@Query("select a from MudAction a where o.cur_state in (0,1) and o.actorCode=? and rownum<=1")
	MudAction findFirstOneByActorCode(Integer actorCode);
	
	MudAction findFirstOneByCurStateAndActorCode(Integer curState, Integer actorCode);
}
