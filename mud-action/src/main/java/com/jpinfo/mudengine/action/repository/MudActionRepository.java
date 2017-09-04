package com.jpinfo.mudengine.action.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.action.model.MudAction;


public interface MudActionRepository extends CrudRepository<MudAction, Long> {

	@Query("select a from MudAction a where a.currState=0 and "
			+ "not exists(select b from MudAction b where b.currState=1 and b.actorCode=a.actorCode) "
			+ "order by a.actionId")
	List<MudAction> findPendingActions();
	
	@Query("select a from MudAction a where "
	+ "(a.currState=1 and a.endTurn<=:currentTurn) or "
	+ "(a.currState=1 and a.actionType=1)")
	List<MudAction> findRunningActions(Long currentTurn);
	
	List<MudAction> findByIssuerCode(Long issuerCode);
	
	@Query("select a from MudAction a where a.currState in (0,1) and a.actorCode=:beingCode")
	List<MudAction> findActiveByActorCode(Long actorCode);

	@Query("select a from MudAction a where a.currState in (0,1) and a.worldName=:worldName and a.placeCode=:placeCode")
	List<MudAction> findActiveByPlace(String worldName, Integer placeCode);
	
	@Query("select a from MudAction a where a.currState in (0,1) and a.actionId=:actionId")
	MudAction findActiveOne(Long actionId);
	
	
	@Query("select a from MudAction a where a.currState=1 and a.endTurn <= ?")
	List<MudAction> findFinishedActions(Long curTurn);
}
