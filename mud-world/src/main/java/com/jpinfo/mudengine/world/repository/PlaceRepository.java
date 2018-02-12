package com.jpinfo.mudengine.world.repository;


import org.springframework.data.repository.CrudRepository;


import com.jpinfo.mudengine.world.model.MudPlace;

public interface PlaceRepository extends CrudRepository<MudPlace, Integer> {

}
