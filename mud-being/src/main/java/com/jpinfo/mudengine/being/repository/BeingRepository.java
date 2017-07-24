package com.jpinfo.mudengine.being.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.being.model.MudBeing;

public interface BeingRepository extends CrudRepository<MudBeing, Long> {

	List<MudBeing> findByPlayerId(Long playerId);
	
	List<MudBeing> findByCurWorldAndCurPlaceCode(String curWorld, Integer curPlaceCode);
}
