package com.jpinfo.mudengine.being.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.being.Being;

@RestController
@RequestMapping("/being")
public class BeingController {
	
	@Autowired
	private BeingRepository repository;

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	public Being getBeing(@PathVariable Long id) {
		
		MudBeing dbBeing = repository.findOne(id);
		
		Being being = BeingHelper.buildBeing(dbBeing);
		
		return being;
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/{id}")
	public void updateBeing(@PathVariable Long id, @RequestBody Being updatedBeing) {
		
		MudBeing dbBeing = repository.findOne(id);
		
		// Basic data
		dbBeing.setName(updatedBeing.getName());
		dbBeing.setPlayerId(updatedBeing.getPlayerId());
		dbBeing.setBeingClass(updatedBeing.getBeingClass());
		dbBeing.setCurPlaceCode(updatedBeing.getCurPlaceCode());
		dbBeing.setCurWorld(updatedBeing.getCurWorld());
		
		// 2. attrModifiers
		dbBeing = BeingHelper.updateBeingAttrModifiers(dbBeing, updatedBeing);
		
		// 3. skillModifiers
		dbBeing = BeingHelper.updateBeingSkillModifiers(dbBeing, updatedBeing);
		
		// 4. items
		dbBeing = BeingHelper.updateBeingItems(dbBeing, updatedBeing);
		
		// Updating the entity
		repository.save(dbBeing);
	}
	
	@RequestMapping(method=RequestMethod.PUT)
	public Being insertBeing(@RequestBody Being newBeing) {

		MudBeing dbBeing = new MudBeing();

		dbBeing.setName(newBeing.getName());
		dbBeing.setPlayerId(newBeing.getPlayerId());
		dbBeing.setBeingClass(newBeing.getBeingClass());
		dbBeing.setCurPlaceCode(newBeing.getCurPlaceCode());
		dbBeing.setCurWorld(newBeing.getCurWorld());
		
		// Saving the entity (to have the beingCode)
		dbBeing = repository.save(dbBeing);
		
		
		// 2. attributes
		dbBeing = BeingHelper.updateBeingAttributes(dbBeing, newBeing);
		
		// 3. attrModifiers
		dbBeing = BeingHelper.updateBeingAttrModifiers(dbBeing, newBeing);
		
		// 4. skillModifiers
		dbBeing = BeingHelper.updateBeingSkillModifiers(dbBeing, newBeing);
		
		// 5. skills
		dbBeing = BeingHelper.updateBeingSkills(dbBeing, newBeing);
		
		// 6. items
		dbBeing = BeingHelper.updateBeingItems(dbBeing, newBeing);
		
		dbBeing = repository.save(dbBeing);
		
		// Convert to the response
		Being response = BeingHelper.buildBeing(dbBeing);
		
		return response;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public Iterable<MudBeing> getAllBeings() {
		
		Iterable<MudBeing> result = repository.findAll();
		
		return result;
	}
	
	@RequestMapping(method=RequestMethod.GET,  value="/player/{playerId}")
	public Iterable<Being> getBeingsForPlayer(@PathVariable Long playerId) {
		
		List<MudBeing> lstFound = repository.findByPlayerId(playerId);
		
		List<Being> response = new ArrayList<Being>();
		
		for(MudBeing curDbBeing: lstFound) {
			response.add(BeingHelper.buildBeing(curDbBeing));
		}
		
		return response;
	}
}
