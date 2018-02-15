package com.jpinfo.mudengine.message.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.jpinfo.mudengine.message.model.MudMessage;

public interface MudMessageRepository extends PagingAndSortingRepository<MudMessage, Long> {
	
	@Query("select a from MudMessage a where a.beingCode=:beingCode and a.readFlag = false")
	public Iterable<MudMessage> findByBeingCode(@Param(value = "beingCode") Long beingCode);

}
