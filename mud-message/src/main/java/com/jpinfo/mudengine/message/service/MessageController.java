package com.jpinfo.mudengine.message.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.service.MessageService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.message.client.BeingServiceClient;
import com.jpinfo.mudengine.message.model.MessageConverter;
import com.jpinfo.mudengine.message.model.MudMessage;
import com.jpinfo.mudengine.message.model.MudMessageParm;
import com.jpinfo.mudengine.message.model.pk.MudMessageParmPK;
import com.jpinfo.mudengine.message.repository.MudMessageRepository;

@RestController
public class MessageController implements MessageService {
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private MudMessageRepository repository;
	
	@Autowired
	private MessageConverter messageConverter;
	

	@Override
	public void putPlainMessage(Long targetCode, String message, Long senderCode, String senderName) {
		
		MudMessage dbMessage = new MudMessage();
		
		dbMessage.setBeingCode(targetCode);
		dbMessage.setMessageKey(message);
		dbMessage.setReadFlag(false);
		dbMessage.setPlainFlag(true);
		dbMessage.setInsertDate(new java.sql.Timestamp(System.currentTimeMillis()));
		
		dbMessage.setSenderCode(senderCode);
		dbMessage.setSenderName(senderName);
		
		repository.save(dbMessage);
	}
	
	@Override
	public void putMessage( 
			@PathVariable("targetCode") Long targetCode, @RequestParam("message") String message, 
			@RequestParam(name="senderCode", required=false) Long senderCode, @RequestParam(name="senderName", required=false) String senderName, 
			@RequestParam(name="parms", required=false) String...parms) {
		
		MudMessage dbMessage = new MudMessage();
		
		dbMessage.setBeingCode(targetCode);
		dbMessage.setMessageKey(message);
		dbMessage.setReadFlag(false);
		dbMessage.setInsertDate(new java.sql.Timestamp(System.currentTimeMillis()));
		
		dbMessage.setSenderCode(senderCode);
		dbMessage.setSenderName(senderName);
		
		dbMessage = repository.save(dbMessage);
		
		
		int evalOrder = 0;
		
		if (parms!=null) {

			for(String curParam: parms) {
				
				MudMessageParm dbParm = new MudMessageParm();
				MudMessageParmPK pk = new MudMessageParmPK();
				
				pk.setMessageId(dbMessage.getMessageId());
				pk.setEvalOrder(evalOrder);
				dbParm.setValue(curParam);
				dbParm.setId(pk);
				
				dbMessage.getParms().add(dbParm);
				
				evalOrder++;
			}
		}
		
		repository.save(dbMessage);
	}

	@Override
	public void broadcastMessage( 
			@PathVariable("placeCode") Integer placeCode, @RequestParam("message") String message, 
			@RequestParam(name="senderCode", required=false) Long senderCode, @RequestParam(name="senderName", required=false) String senderName, 
			@RequestParam(name="parms", required=false) String...parms) {
		
		// Select all beings from a place
		// @TODO Solve the worldName
		List<Being> allBeingFromPlace = beingService.getAllFromPlace("aforgotten", placeCode);
		
		for(Being curBeing: allBeingFromPlace) {
			
			if (curBeing.getType().equals(Being.enumBeingType.PLAYABLE)) {
				putMessage(curBeing.getCode(), message, senderCode, senderName, parms);
			}
		}
	}
	
	
	@Override
	public List<Message> getMessage(
			@RequestParam(name="allMessages", defaultValue="false", required=false) Boolean allMessages,
			@RequestParam(name="pageCount", defaultValue="0", required=false) Integer pageCount,
			@RequestParam(name="pageSize", defaultValue="10", required=false) Integer pageSize) {
		
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
			
			if (allMessages) {
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
						return d.getPlainFlag() ? messageConverter.build(d) : messageConverter.build(d, callerLocale);
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
