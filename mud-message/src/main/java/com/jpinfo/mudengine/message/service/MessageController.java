package com.jpinfo.mudengine.message.service;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.service.MessageService;
import com.jpinfo.mudengine.message.client.BeingServiceClient;
import com.jpinfo.mudengine.message.model.MudMessage;
import com.jpinfo.mudengine.message.model.MudMessageLocale;
import com.jpinfo.mudengine.message.model.MudMessageParm;
import com.jpinfo.mudengine.message.model.pk.MudMessageLocalePK;
import com.jpinfo.mudengine.message.model.pk.MudMessageParmPK;
import com.jpinfo.mudengine.message.repository.MudMessageLocaleRepository;
import com.jpinfo.mudengine.message.repository.MudMessageRepository;
import com.jpinfo.mudengine.message.utils.MessageHelper;

@RestController
public class MessageController implements MessageService {
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private MudMessageRepository repository;
	
	@Autowired
	private MudMessageLocaleRepository localeRepository;

	@Override
	public void putMessage(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
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
	public void broadcastMessage(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@PathVariable("placeCode") Integer placeCode, @RequestParam("message") String message, 
			@RequestParam(name="senderCode", required=false) Long senderCode, @RequestParam(name="senderName", required=false) String senderName, 
			@RequestParam(name="parms", required=false) String...parms) {
		
		// Select all beings from a place
		List<Being> allBeingFromPlace = beingService.getAllFromPlace("aforgotten", placeCode);
		
		for(Being curBeing: allBeingFromPlace) {
			
			if (curBeing.getBeingType() == Being.BEING_TYPE_PLAYER) {
				putMessage(authToken, curBeing.getBeingCode(), message, senderCode, senderName, parms);
			}
		}
	}
	
	
	@Override
	public List<Message> getMessage(@RequestHeader(TokenService.HEADER_TOKEN) String authToken,
			@RequestParam(name="allMessages", defaultValue="false", required=false) Boolean allMessages,
			@RequestParam(name="pageCount", defaultValue="0", required=false) Integer pageCount,
			@RequestParam(name="pageSize", defaultValue="10", required=false) Integer pageSize) {
		
		List<Message> result = new ArrayList<Message>();
		
		PageRequest page = PageRequest.of(pageCount, pageSize, Sort.Direction.DESC, "insertDate");
		
		// Obtaining the caller locale
		String callerLocale = TokenService.getLocaleFromToken(authToken);
		
		// Obtaining the beingCode
		Long beingCode = TokenService.getBeingCodeFromToken(authToken);
		
		if (beingCode!=null) {
			Page<MudMessage> lstMessage = null;
			
			if (allMessages) {
				lstMessage = repository.findByBeingCode(beingCode, page);
			} else {
				lstMessage = repository.findUnreadByBeingCode(beingCode, page);
			}
			
			for(MudMessage curDbMessage: lstMessage.getContent()) {
				
				result.add(buildMessage(curDbMessage, callerLocale));

				// Mark the message as read
				curDbMessage.setReadFlag(true);
			}
			
			// Save all message changes
			repository.saveAll(lstMessage);
			
		} else {
			throw new IllegalParameterException("Being not set in header");
		}
		
		return result;
	}

	
	private Message buildMessage(MudMessage a, String callerLocale) {
		
		Message result = new Message();
		List<Object> parmsList = new ArrayList<Object>();
		String actualMessage = a.getMessageKey();
		
		// Verify if the messageKey is a string table entry
		if (MessageHelper.isLocalizedKey(actualMessage)) {
			
			MudMessageLocalePK pk = new MudMessageLocalePK();
			pk.setMessageKey(MessageHelper.getLocalizedKey(a.getMessageKey()));
			pk.setLocale(callerLocale);
		
			Optional<MudMessageLocale> dbLocalizedMessage = localeRepository.findById(pk);
			
			if (dbLocalizedMessage.isPresent()) {
				actualMessage = dbLocalizedMessage.get().getMessageText();
			}
				
		}

		// Build an object array with all parameters
		for(MudMessageParm curParm: a.getParms()) {

			// Verify if the param value is a string table entry
			if (MessageHelper.isLocalizedKey(curParm.getValue().toString())) {

				MudMessageLocalePK pk = new MudMessageLocalePK();
				pk.setMessageKey(MessageHelper.getLocalizedKey(curParm.getValue().toString()));
				pk.setLocale(callerLocale);
			
				Optional<MudMessageLocale> dbLocalizedMessage = localeRepository.findById(pk);
				
				if (dbLocalizedMessage.isPresent()) {
					parmsList.add(dbLocalizedMessage.get().getMessageText());
				} else {
					parmsList.add("Value key " + pk.getMessageKey() + " not found in " + callerLocale + " locale");
				}
			} else {
				parmsList.add(curParm.getValue().toString());
			}
		}
		
		if (parmsList.size()>0) {
			// Format the message
			actualMessage = String.format(actualMessage, parmsList.toArray());			
		}
		
		result.setSenderCode(a.getSenderCode());
		result.setSenderName(a.getSenderName());

		DateFormat df = 
				DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, 
						Locale.forLanguageTag(callerLocale));
		
		result.setMessageDate(df.format(a.getInsertDate()));
		result.setMessage(actualMessage);

		return result;
	}

}
