package com.jpinfo.mudengine.action.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.action.model.MudActionState;

public interface MudActionStateRepository extends CrudRepository<MudActionState, Long> {
	
	List<MudActionState> findByActionIssuerCode(Integer issuerCode);

}
