package com.jpinfo.mudengine.action.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jpinfo.mudengine.action.model.MudAction;


public interface MudActionRepository extends CrudRepository<MudAction, Long> {

	@Query("select a from MudAction a where a.currState=0 and "
			+ "not exists(select b from MudAction b where b.currState=1 and b.actorCode=a.actorCode) "
			+ "order by a.actionId")
	List<MudAction> findPendingActions();

	@Query("select a from MudAction a, MudActionClass b where "
	+ "    b.actionClassCode = a.actionClassCode and" 
	+ "((a.currState=1 and a.endTurn<= :currentTurn) or "
	+ "(a.currState=1 and b.actionType=1))")
	List<MudAction> findRunningActions(@Param("currentTurn") Long currentTurn);
	
	List<MudAction> findByIssuerCode(Long issuerCode);
	
	@Query("select a from MudAction a where a.currState in (0,1) and a.actorCode=:actorCode")
	List<MudAction> findActiveByActorCode(@Param("actorCode") Long actorCode);

	@Query("select a from MudAction a where a.currState in (0,1) and a.actionId=:actionId")
	MudAction findActiveOne(@Param("actionId") Long actionId);
	
	
	@Query("select a from MudAction a where a.currState=1 and a.endTurn <= :curTurn")
	List<MudAction> findFinishedActions(@Param("curTurn") Long curTurn);
}
