package com.jpinfo.mudengine.message.model.converter;

import java.text.DateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.message.model.MudMessage;
import com.jpinfo.mudengine.message.model.MudMessageLocale;
import com.jpinfo.mudengine.message.model.pk.MudMessageLocalePK;
import com.jpinfo.mudengine.message.repository.MudMessageLocaleRepository;

@Component
public class MessageConverter {
	
	private static final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	
	@Autowired
	private MudMessageLocaleRepository localeRepository;
	
	public Message build(MudMessage dbMessage, String callerLocale) {
		
		Message result = new Message();
		
		// Get (and localize) the main message
		String actualMessage = getLocalizedMessage(dbMessage.getMessageKey(), callerLocale);
		
		// Build a string array with all parameters
		List<Object> parmsList = 
				dbMessage.getParms().stream()
				.map(d -> getLocalizedMessage(d.getValue(), callerLocale))
				.collect(Collectors.toList());
			
		// Format the message
		result.setContent(
				String.format(actualMessage, parmsList.toArray())
				);
		
		result.setSenderCode(dbMessage.getSenderCode());
		result.setSenderName(dbMessage.getSenderName());

		result.setMessageDate(df.format(dbMessage.getInsertDate()));

		return result;

	}
	
	public Message build(MudMessage dbMessage) {
		
		Message result = new Message();

		// Get the main message without localization
		result.setContent(dbMessage.getMessageKey());
		result.setSenderCode(dbMessage.getSenderCode());
		result.setSenderName(dbMessage.getSenderName());

		result.setMessageDate(df.format(dbMessage.getInsertDate()));

		return result;
	}
	
	
	private String getLocalizedMessage(String originalValue, String callerLocale) {
		
		MudMessageLocalePK pk = new MudMessageLocalePK();
		pk.setMessageKey(originalValue);
		pk.setLocale(callerLocale);
	
		Optional<MudMessageLocale> dbLocalizedMessage = 
				localeRepository.findById(pk);
				
		if (dbLocalizedMessage.isPresent()) {
			return dbLocalizedMessage.get().getMessageText();
		} else {
			return originalValue;
		}
	}
}
