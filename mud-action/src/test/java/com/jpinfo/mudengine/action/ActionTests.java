package com.jpinfo.mudengine.action;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.Test;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.dto.ActionInfo;
import com.jpinfo.mudengine.action.service.ActionHandler;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.Action.EnumActionState;
import com.jpinfo.mudengine.common.action.Action.EnumTargetType;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.placeclass.PlaceClass;

import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8"})
public class ActionTests {
	
	private static final Integer START_PLACE_CODE = 1;
	private static final Integer END_PLACE_CODE = 2;
	
	@Autowired
	private ActionHandler handler;
	
	@MockBean
	private BeingServiceClient beingClient;
	
	@MockBean
	private ItemServiceClient itemClient;
	
	@MockBean
	private PlaceServiceClient placeClient;

	@Test
	public void contextLoads() {
	}
	
	private Being getBeingOne() {
		Being beingOne = new Being();
		
		beingOne.setType(Being.enumBeingType.REGULAR_SENTIENT);
		beingOne.setCode(1L);
		beingOne.setClassCode("HUMAN");
		beingOne.setName("Tori");
		beingOne.setPlayerId(1L);
		beingOne.setCurPlaceCode(ActionTests.START_PLACE_CODE);
		beingOne.setCurWorld("aforgotten");
		beingOne.setQuantity(1);
		
		BeingClass beingClassOne = new BeingClass();
		beingClassOne.setName("Human beings");
		
		beingOne.setBeingClass(beingClassOne);		
		
		return beingOne;
	}
	
	private Being getBeingTwo() {
		Being beingTwo = new Being();
		
		beingTwo.setType(Being.enumBeingType.REGULAR_SENTIENT);
		beingTwo.setCode(2L);
		beingTwo.setClassCode("HUMAN");
		beingTwo.setName("Uke");
		beingTwo.setPlayerId(1L);
		beingTwo.setCurPlaceCode(ActionTests.START_PLACE_CODE);
		beingTwo.setCurWorld("aforgotten");
		beingTwo.setQuantity(1);
		
		BeingClass beingClassTwo = new BeingClass();
		beingClassTwo.setName("Human beings");
		
		beingTwo.setBeingClass(beingClassTwo);
		
		
		return beingTwo;
	}	

	private Place getPlaceOne() {
		Place placeOne = new Place();
		placeOne.setCode(ActionTests.START_PLACE_CODE);
		placeOne.setClassCode("PLAIN");
		
		// Adding the north exit to place 2
		PlaceExit northExit = new PlaceExit();
		northExit.setTargetPlaceCode(ActionTests.END_PLACE_CODE);
		northExit.setOpened(true);
		placeOne.getExits().put("NORTH", northExit);
		
		PlaceClass placeClassOne = new PlaceClass();
		placeClassOne.setName("Plain");
		placeClassOne.setDescription("Plain land");
		
		placeOne.setPlaceClass(placeClassOne);

		return placeOne;
	}

	private void setupMocks() {
		
		given(beingClient.getBeing(eq(1L))).willReturn(getBeingOne());
		given(beingClient.getBeing(eq(2L))).willReturn(getBeingTwo());
		given(placeClient.getPlace(eq(1))).willReturn(getPlaceOne());
		
	}
	
	@Test
	public void testWalkDir() {
		
		setupMocks();
		
		Action walkAction = new Action();
		walkAction.setActorCode(1L);
		walkAction.setActionClassCode(1);   // WALKDIR
		walkAction.setTargetCode("NORTH");
		walkAction.setTargetType(EnumTargetType.DIRECTION);

		ActionInfo testData = handler.buildActionInfo(walkAction);

		// ************ UPDATE ACTION *****************
		// =============================================
		handler.updateAction(1L, walkAction, testData);
		
		// Assert testData values
		assertThat(walkAction.getEndTurn()).isNotNull();
		assertThat(walkAction.getCurState()).isEqualTo(EnumActionState.STARTED);
		
		// Finishing the action
		handler.updateAction(walkAction.getEndTurn(), walkAction, testData);
		
		assertThat(walkAction.getCurState()).isEqualTo(EnumActionState.COMPLETED);
		assertThat(testData.getActor().getBeing().getCurPlaceCode()).isEqualTo(ActionTests.END_PLACE_CODE);
	}	
	
	@Test
	public void testLookPlace() {
		
		setupMocks();
		
		Action lookAction = new Action();
		lookAction.setActorCode(1L);
		lookAction.setActionClassCode(2);  // LOOKPLACE
		lookAction.setTargetCode("1");
		lookAction.setTargetType(EnumTargetType.PLACE);

		ActionInfo testData = handler.buildActionInfo(lookAction);

		// ************ UPDATE ACTION *****************
		// =============================================
		handler.updateAction(1L, lookAction, testData);
		
		// Assert testData values
		assertThat(lookAction.getEndTurn()).isNotNull();
		assertThat(lookAction.getCurState()).isEqualTo(EnumActionState.COMPLETED);

		assertThat(testData.getActor().getMessages().isEmpty()).isFalse();
	}	

	@Test
	public void testLookBeing() {
		
		setupMocks();
		
		Action lookAction = new Action();
		lookAction.setActorCode(1L);
		lookAction.setActionClassCode(3);   //LOOKBEING
		lookAction.setTargetCode("2");
		lookAction.setTargetType(EnumTargetType.BEING);

		ActionInfo testData = handler.buildActionInfo(lookAction);

		// ************ UPDATE ACTION *****************
		// =============================================
		handler.updateAction(1L, lookAction, testData);
		
		// Assert testData values
		assertThat(lookAction.getEndTurn()).isNotNull();
		assertThat(lookAction.getCurState()).isEqualTo(EnumActionState.COMPLETED);

		assertThat(testData.getActor().getMessages().isEmpty()).isFalse();
	}	

}
