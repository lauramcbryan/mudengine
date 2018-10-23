package com.jpinfo.mudengine.world.repository;


import org.springframework.data.repository.CrudRepository;


import com.jpinfo.mudengine.world.model.MudPlace;

public interface PlaceRepository extends CrudRepository<MudPlace, Integer> {

	/**
	 * Just to AOP pointcut to be able to grab this
	 */
	@Override
	<S extends MudPlace> S save(S entity);

}
