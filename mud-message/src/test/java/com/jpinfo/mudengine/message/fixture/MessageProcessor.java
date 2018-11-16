package com.jpinfo.mudengine.message.fixture;

import com.jpinfo.mudengine.message.model.MudMessage;

import br.com.six2six.fixturefactory.processor.Processor;

public class MessageProcessor implements Processor {
	
	private Long beingCode;
	
	public MessageProcessor(Long beingCode) {
		
		this.beingCode = beingCode;
	}

	@Override
	public void execute(Object result) {
		
		if (result instanceof MudMessage) {
			
			MudMessage messageResult = (MudMessage)result;
			
			messageResult.setBeingCode(beingCode);
		}
		

	}

}
