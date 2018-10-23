package com.jpinfo.mudengine.item.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.item.model.MudItem;

public interface ItemRepository extends CrudRepository<MudItem, Long> {
	
	List<MudItem> findByCurOwner(Long curOwner);
	
	List<MudItem> findByCurWorldAndCurPlaceCode(String curWorld, Integer curPlace);

	@Override
	<S extends MudItem> S save(S entity);


}
