package com.jpinfo.mudengine.message.model.converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.jpinfo.mudengine.message.model.MudMessageParm;
import com.jpinfo.mudengine.message.model.pk.MudMessageParmPK;

public class MudMessageParmConverter {

	private MudMessageParmConverter() {}
	
	public static Set<MudMessageParm> build(final Long messageId, final String[] parms) {
		
		AtomicInteger evalOrderIndex = new AtomicInteger();
		
		if (parms!=null) {
			
			return 
				Arrays.stream(parms)
					.map(d -> build(evalOrderIndex, messageId, d))
					.collect(Collectors.toSet());
		} else { 
			return new HashSet<>();
		}
	}
	
	private static MudMessageParm build(AtomicInteger evalOrderIndex, final Long messageId, final String parm) {
		
		MudMessageParm dbParm = new MudMessageParm();
		MudMessageParmPK pk = new MudMessageParmPK();
		
		pk.setMessageId(messageId);
		pk.setEvalOrder(evalOrderIndex.getAndIncrement());
		dbParm.setValue(parm);
		dbParm.setId(pk);
		
		return dbParm;
	}
	
}
