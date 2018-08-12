package com.jpinfo.mudengine.world.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.model.pk.MudPlaceExitPK;

public interface PlaceExitRepository extends CrudRepository<MudPlaceExit, MudPlaceExitPK> {

	public Iterable<MudPlaceExit> findByTargetPlaceCode(Integer targetPlaceCode);
}
