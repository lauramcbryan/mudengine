package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeingSlot;
import com.jpinfo.mudengine.being.model.pk.MudBeingSlotPK;

public class MudBeingSlotConverter {

	private MudBeingSlotConverter() { }
	
	public static MudBeingSlot build(Long beingCode, String slotCode) {
		
		MudBeingSlot dbSlot = new MudBeingSlot();
		MudBeingSlotPK dbSlotPK = new MudBeingSlotPK();
		
		dbSlotPK.setBeingCode(beingCode);
		dbSlotPK.setSlotCode(slotCode);
		
		dbSlot.setId(dbSlotPK);
		
		return dbSlot;
	}
}
