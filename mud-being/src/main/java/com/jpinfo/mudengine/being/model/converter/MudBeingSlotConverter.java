package com.jpinfo.mudengine.being.model.converter;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassSlot;
import com.jpinfo.mudengine.being.model.MudBeingSlot;
import com.jpinfo.mudengine.being.model.pk.MudBeingSlotPK;

public class MudBeingSlotConverter {

	private MudBeingSlotConverter() { }
	
	public static MudBeingSlot build(Long beingCode, String slotCode) {
		
		MudBeingSlot dbSlot = new MudBeingSlot();
		MudBeingSlotPK dbSlotPK = new MudBeingSlotPK();
		
		dbSlotPK.setBeingCode(beingCode);
		dbSlotPK.setCode(slotCode);
		
		dbSlot.setId(dbSlotPK);
		
		return dbSlot;
	}
	
	public static MudBeingSlot convert(Long beingCode, MudBeingClassSlot classSlot) {

		MudBeingSlot dbSlot = new MudBeingSlot();
		MudBeingSlotPK dbSlotPK = new MudBeingSlotPK();
		
		dbSlotPK.setBeingCode(beingCode);
		dbSlotPK.setCode(classSlot.getId().getCode());
		
		dbSlot.setId(dbSlotPK);
		
		return dbSlot;
		
	}
	
	public static MudBeing sync(MudBeing dbBeing, MudBeingClass previousClass, MudBeingClass nextClass) {

		if (previousClass!=null) {
			
			// Looking for attributes to remove
			dbBeing.getSlots().removeIf(d -> {
				
				boolean existsInOldClass = previousClass.getSlots().stream()
						.anyMatch(e -> d.getId().getCode().equals(e.getId().getCode()));
				
				boolean existsInNewClass = nextClass.getSlots().stream()
						.anyMatch(e -> d.getId().getCode().equals(e.getId().getCode()));
				
				return existsInOldClass && ! existsInNewClass;
			});
		}
		
		// Looking for attributes to add/update
		if (nextClass.getSlots()!=null) {
			nextClass.getSlots().stream()
				.forEach(d -> {
	
					MudBeingSlot slot = 
						dbBeing.getSlots().stream()
							.filter(e -> e.getId().getCode().equals(d.getId().getCode()))
							.findFirst()
							.orElse(MudBeingSlotConverter.convert(dbBeing.getCode(), d));
	
					dbBeing.getSlots().add(slot);
					
				});
		}
		
		return dbBeing;
		
	}
}
