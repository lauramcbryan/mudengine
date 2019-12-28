package com.jpinfo.mudengine.message.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.message.client.BeingServiceClient;
import com.jpinfo.mudengine.message.model.MudMessage;
import com.jpinfo.mudengine.message.model.converter.MessageConverter;
import com.jpinfo.mudengine.message.model.converter.MudMessageEntityConverter;
import com.jpinfo.mudengine.message.model.converter.MudMessageParmConverter;
import com.jpinfo.mudengine.message.repository.MudMessageRepository;

@Service
public class MessageServiceImpl {

	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private MudMessageRepository repository;
	
	@Autowired
	private MessageConverter messageConverter;
	
	public Long putMessage(Long targetCode,MessageRequest request) {
		
		MudMessage dbMessage = new MudMessage();
		
		dbMessage.setBeingCode(targetCode);
		dbMessage.setMessageKey(request.getMessageKey());
		dbMessage.setReadFlag(false);
		dbMessage.setInsertDate(new java.sql.Timestamp(System.currentTimeMillis()));
		
		dbMessage.setSenderCode(request.getSenderCode());
		dbMessage.setSenderName(request.getSenderName());
		
		// Persisting the record to have the messageId
		dbMessage = repository.save(dbMessage);
		
		// Declaring a final variable just to be able to use it in
		// streams below
		final Long messageId = dbMessage.getMessageId();
		
		// Set the params, if the exist
		dbMessage.getParms().addAll(MudMessageParmConverter.build(messageId, request.getArgs()));
		
		
		// Set the changed entities, if they exist
		if (request.getChangedEntities()!=null) {
			
			dbMessage.getEntities().addAll(
					request.getChangedEntities().stream()
						.map(d ->
							MudMessageEntityConverter.build(messageId, d)
						)
					.collect(Collectors.toList())
			);
		}
		
		// Update the record in database, now fully formed
		repository.save(dbMessage);
		
		return dbMessage.getMessageId();
	}

	public List<Long> broadcastMessage(Integer placeCode, MessageRequest request) {
		
		List<Long> resultList = new ArrayList<>();
		
		MudUserDetails uDetails = (MudUserDetails)
				SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		uDetails.getSessionData().ifPresent(d -> {
			
			// Select all beings from a place
			List<Being> allBeingsFromPlace = beingService.getAllFromPlace(d.getCurWorldName(), placeCode);
			
			allBeingsFromPlace.stream()
				.filter(e -> e.getPlayerId()!=null)
				.forEach(e ->
					resultList.add(
							putMessage(e.getCode(), request)
							)
				);
		});
		
		return resultList;
	}
	
	
	public List<Message> getMessage(Boolean allMessages, Integer pageCount, Integer pageSize) {
		
		List<Message> result;
		
		PageRequest page = PageRequest.of(pageCount, pageSize, Sort.Direction.DESC, "insertDate");
		
		MudUserDetails uDetails = (MudUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		// Obtaining the caller locale
		Optional<Player> playerData = uDetails.getPlayerData();
		
		String callerLocale = playerData.isPresent() ? 
				playerData.get().getLocale(): 
				CommonConstants.DEFAULT_LOCALE;

		// Obtaining the beingCode
		Optional<Session> sessionData = uDetails.getSessionData();
		
		Long beingCode = sessionData.isPresent() ?
				sessionData.get().getBeingCode(): null;
				
				
		if (beingCode!=null) {
			
			Page<MudMessage> lstMessage = null;
			
			if (allMessages.booleanValue()) {
				lstMessage = repository.findByBeingCode(beingCode, page);
			} else {
				lstMessage = repository.findUnreadByBeingCode(beingCode, page);
			}
			
			result = 
				lstMessage.getContent().stream()
					.map(d -> {
						
						// Mark the message as read						
						d.setReadFlag(true);
						
						// Convert (and localizes) the message
						return messageConverter.build(d, callerLocale);
					})
					.collect(Collectors.toList());
			
			// Save all message changes
			repository.saveAll(lstMessage);
			
		} else {
			throw new IllegalParameterException(LocalizedMessages.SESSION_BEING_NOT_FOUND);
		}
		
		return result;
	}

}
