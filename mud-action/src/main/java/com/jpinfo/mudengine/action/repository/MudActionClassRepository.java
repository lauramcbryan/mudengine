package com.jpinfo.mudengine.action.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.action.model.MudActionClass;

public interface MudActionClassRepository extends CrudRepository<MudActionClass, String> {

	public MudActionClass findByVerb(String verb);
}
