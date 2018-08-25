package com.jpinfo.mudengine.action.dto;

import java.util.List;

import com.jpinfo.mudengine.action.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BeingComposite implements ActionTarget  {

	private Being being;
	
	private List<Item> items;
	
	private Place place;
}
