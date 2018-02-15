package com.jpinfo.mudengine.message.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	public void putMessage(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("targetCode") Long targetCode, @RequestParam("message") String message, @RequestParam(name="parms", required=false) String...parms) {
		
		MudMessage dbMessage = new MudMessage();
		
		dbMessage.setBeingCode(targetCode);
		dbMessage.setMessageKey(message);
		dbMessage.setReadFlag(false);
		
		dbMessage.setSenderCode(null);
		dbMessage.setSenderName(null);
		
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
	public void broadcastMessage(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("placeCode") Integer placeCode, @RequestParam("message") String message, @RequestParam(name="parms", required=false) String...parms) {
		
		// Select all beings from a place
		List<Being> allBeingFromPlace = beingService.getAllFromPlace("aforgotten", placeCode);
		
		for(Being curBeing: allBeingFromPlace) {
			
			if (curBeing.getBeingType() == Being.BEING_TYPE_PLAYER) {
				putMessage(authToken, curBeing.getBeingCode(), message, parms);
			}
		}
	}
	
	
	@Override
	public List<Message> getMessage(@RequestHeader(TokenService.HEADER_TOKEN) String authToken) {
		
		List<Message> result = new ArrayList<Message>();
		
		// Obtaining the caller locale
		String callerLocale = TokenService.getLocaleFromToken(authToken);
		
		// Obtaining the beingCode
		Long beingCode = TokenService.getBeingCodeFromToken(authToken);
		
		if (beingCode!=null) {
			Iterable<MudMessage> lstMessage = repository.findByBeingCode(beingCode);
			
			for(MudMessage curDbMessage: lstMessage) {
				
				result.add(buildMessage(curDbMessage, callerLocale));

				// Mark the message as read
				curDbMessage.setReadFlag(true);
			}
			
			// Save all message changes
			repository.save(lstMessage);
			
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
		
			MudMessageLocale dbLocalizedMessage = localeRepository.findOne(pk);
			
			if (dbLocalizedMessage!=null) {
				actualMessage = dbLocalizedMessage.getMessageText();
			} else {
				actualMessage = "Message key " + pk.getMessageKey() + " not found in " + callerLocale + " locale";
			}
		}

		// Build an object array with all parameters
		for(MudMessageParm curParm: a.getParms()) {

			// Verify if the param value is a string table entry
			if (MessageHelper.isLocalizedKey(curParm.getValue().toString())) {

				MudMessageLocalePK pk = new MudMessageLocalePK();
				pk.setMessageKey(MessageHelper.getLocalizedKey(curParm.getValue().toString()));
				pk.setLocale(callerLocale);
			
				MudMessageLocale dbLocalizedMessage = localeRepository.findOne(pk);
				
				if (dbLocalizedMessage!=null) {
					parmsList.add(dbLocalizedMessage.getMessageText());
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
		result.setInsertTurn(a.getInsertTurn());
		result.setMessage(actualMessage);

		return result;
	}

}
