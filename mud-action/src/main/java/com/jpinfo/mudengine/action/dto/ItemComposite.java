package com.jpinfo.mudengine.action.dto;


import com.jpinfo.mudengine.action.interfaces.ActionTarget;

import com.jpinfo.mudengine.common.item.Item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemComposite implements ActionTarget {
	
	private Item innerItem;
}
