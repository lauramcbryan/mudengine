package com.jpinfo.mudengine.being.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.being.model.MudBeing;

public interface BeingRepository extends CrudRepository<MudBeing, Long> {

	List<MudBeing> findByPlayerId(Long playerId);
	
	Optional<MudBeing> findByName(String name);
	
	@Query("select o from MudBeing o where "
			+ "o.playerId is not null and "
			+ "o.curWorld=:curWorld and "
			+ "o.curPlaceCode=:curPlaceCode")
	List<MudBeing> findPlayableInThisPlace(String curWorld, Integer curPlaceCode);
	
	List<MudBeing> findByCurWorldAndCurPlaceCode(String curWorld, Integer curPlaceCode);
}
