package com.jpinfo.mudengine.world.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.model.pk.PlaceExitPK;

public interface PlaceExitRepository extends CrudRepository<MudPlaceExit, PlaceExitPK> {

	public Iterable<MudPlaceExit> findByTargetPlaceCode(Integer targetPlaceCode);
}
