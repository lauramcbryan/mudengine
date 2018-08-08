package com.jpinfo.mudengine.item.fixture;

import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.converter.MudItemAttrConverter;

import br.com.six2six.fixturefactory.processor.Processor;

public class MudItemProcessor implements Processor {

	@Override
	public void execute(Object result) {
		
		if (result instanceof MudItem) {
			
			MudItem mock = (MudItem)result;
			
			mock.getAttrs().clear();
			mock.getItemClass().getAttrs().stream()
				.forEach(e -> {
					mock.getAttrs().add(MudItemAttrConverter.build(mock.getItemCode(), e));
				});
			
		}

	}

}
