package com.jpinfo.mudengine.item.fixture;

import com.jpinfo.mudengine.item.model.MudItemClass;

import br.com.six2six.fixturefactory.processor.Processor;

public class DemisableItemProcessor implements Processor {
	
	private String anotherClass;
	
	public DemisableItemProcessor(String anotherClass) {
		this.anotherClass = anotherClass;
	}

	@Override
	public void execute(Object result) {
		
		if (result instanceof MudItemClass) {
			((MudItemClass)result).setDemiseItemClassCode(anotherClass);
		}
	}

}
