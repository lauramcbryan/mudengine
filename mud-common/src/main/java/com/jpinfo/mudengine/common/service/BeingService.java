package com.jpinfo.mudengine.common.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jpinfo.mudengine.common.being.Being;

@RequestMapping("/being")
public interface BeingService {

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	Being getBeing(@PathVariable("id") Long id);

	@RequestMapping(method=RequestMethod.POST, value="/{id}")
	void updateBeing(@PathVariable("id") Long id, @RequestBody Being updatedBeing);

	@RequestMapping(method=RequestMethod.PUT)
	Being insertBeing(@RequestBody Being newBeing);

	@RequestMapping(method=RequestMethod.GET,  value="/player/{playerId}")
	Iterable<Being> getBeingsForPlayer(@PathVariable("playerId") Long playerId);

}