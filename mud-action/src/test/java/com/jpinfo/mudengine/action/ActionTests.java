package com.jpinfo.mudengine.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionHandler;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.action.utils.ActionInfo;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.Action.EnumActionState;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class ActionTests {
	
	@Autowired
	private ActionHandler handler;
	
	@Autowired
	private MudActionRepository repository;
	
	@MockBean
	private BeingServiceClient beingClient;
	
	@MockBean
	private ItemServiceClient itemClient;
	
	@MockBean
	private PlaceServiceClient placeClient;

	@Test
	public void contextLoads() {
	}
	
	public void testWalk() {
		
		MudAction dbAction = repository.findOne(1L);
		
		Action walkAction = ActionHelper.buildAction(dbAction);
		
		ActionInfo testData = new ActionInfo();
		
		// Create the being
		Being beingOne = new Being();
		
		beingOne.setBeingType(1);
		beingOne.setBeingCode(1L);
		beingOne.setBeingClass("HUMAN");
		beingOne.setName("Tori");
		beingOne.setPlayerId(1L);
		beingOne.setCurPlaceCode(1);
		beingOne.setCurWorld("aforgotten");
		beingOne.setQuantity(1);
		
		
		// Create the firstPlace
		Place placeOne = new Place();
		placeOne.setPlaceCode(1);
		placeOne.setPlaceClassCode("PLAIN");
		
		// Adding the north exit to place 2
		PlaceExit northExit = new PlaceExit();
		northExit.setTargetPlaceCode(2);
		placeOne.getExits().put("NORTH", northExit);

		testData.setActorCode(beingOne.getBeingCode());
		testData.setActor(beingOne);		
		testData.setWorldName("aforgotten");
		testData.setPlace(placeOne);
		testData.setPlaceCode(placeOne.getPlaceCode());
		
		testData.setTargetCode("NORTH");
		
		
		// ************ UPDATE ACTION *****************
		// =============================================
		handler.updateAction(1L, walkAction, testData);
		
		// Assert testData values
		assertThat(walkAction.getEndTurn()).isNotNull();
		assertThat(walkAction.getCurState()).isEqualTo(EnumActionState.STARTED);
		
		
		handler.updateAction(2L, walkAction, testData);
		
		// Finishing the action
		handler.updateAction(walkAction.getEndTurn(), walkAction, testData);
		
	}	
}
