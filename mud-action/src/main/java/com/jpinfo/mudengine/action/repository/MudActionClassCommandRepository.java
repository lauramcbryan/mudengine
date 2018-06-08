package com.jpinfo.mudengine.action.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jpinfo.mudengine.action.model.MudActionClassCommand;

public interface MudActionClassCommandRepository extends CrudRepository<MudActionClassCommand, Integer> {
	
	public List<MudActionClassCommand> findByLocale(String locale);
	

}
