package com.jpinfo.mudengine.being.repository;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.jpinfo.mudengine.being.model.MudBeingMessage;
import com.jpinfo.mudengine.being.model.pk.MudBeingMessagePK;

public interface BeingMessageRepository extends PagingAndSortingRepository<MudBeingMessage, MudBeingMessagePK> {

	@Query("select a from MudBeingMessage a where a.id.beingCode=:beingCode and a.readFlag is false order by a.id.createDate")
	Collection<MudBeingMessage> findUnreadMessages(Long beingCode);
	
	@Query("select a from MudBeingMessage a where a.id.beingCode=:beingCode and a.id.createDate between :sinceDate and :untilDate order by a.id.createDate")
	Collection<MudBeingMessage> findRangedMessages(Long beingCode, Date sinceDate, Date untilDate);
	
	@Query("delete from MudBeingMessage a where a.id.beingCode=:beingCode and a.readFlag is true")
	void deleteReadMessages(Long beingCode);
}
