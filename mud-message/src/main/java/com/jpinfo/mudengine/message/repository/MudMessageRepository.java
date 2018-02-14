package com.jpinfo.mudengine.message.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.jpinfo.mudengine.message.model.MudMessage;

public interface MudMessageRepository extends PagingAndSortingRepository<MudMessage, Long> {
	
	public Iterable<MudMessage> findByBeingCode(Long beingCode);

}
